import SwaggerParser from 'swagger-parser';
import { 
  OpenAPISpec, 
  GeneratedEntity, 
  EntityProperty, 
  EntityOperation, 
  Schema,
  Operation,
  PathItem 
} from '../types';

export class OpenAPIParser {
  private spec: OpenAPISpec;

  constructor(spec: OpenAPISpec) {
    this.spec = spec;
  }

  static async fromUrl(url: string): Promise<OpenAPIParser> {
    try {
      const spec = await SwaggerParser.dereference(url) as OpenAPISpec;
      return new OpenAPIParser(spec);
    } catch (error) {
      throw new Error(`Failed to parse OpenAPI spec from ${url}: ${error}`);
    }
  }

  static async fromFile(filePath: string): Promise<OpenAPIParser> {
    try {
      const spec = await SwaggerParser.dereference(filePath) as OpenAPISpec;
      return new OpenAPIParser(spec);
    } catch (error) {
      throw new Error(`Failed to parse OpenAPI spec from ${filePath}: ${error}`);
    }
  }

  getEntities(): GeneratedEntity[] {
    const entities: GeneratedEntity[] = [];
    const entityMap = new Map<string, GeneratedEntity>();

    // Group operations by tags
    Object.entries(this.spec.paths).forEach(([path, pathItem]) => {
      Object.entries(pathItem).forEach(([method, operation]) => {
        if (this.isHttpMethod(method) && operation) {
          const tag = this.getMainTag(operation);
          if (tag) {
            this.addOperationToEntity(entityMap, tag, method.toUpperCase(), path, operation);
          }
        }
      });
    });

    return Array.from(entityMap.values());
  }

  private isHttpMethod(method: string): boolean {
    return ['get', 'post', 'put', 'delete', 'patch'].includes(method.toLowerCase());
  }

  private getMainTag(operation: Operation): string | null {
    if (operation.tags && operation.tags.length > 0) {
      return operation.tags[0];
    }
    return null;
  }

  private addOperationToEntity(
    entityMap: Map<string, GeneratedEntity>,
    tag: string,
    method: string,
    path: string,
    operation: Operation
  ): void {
    const entityName = this.sanitizeEntityName(tag);
    
    if (!entityMap.has(entityName)) {
      entityMap.set(entityName, {
        name: entityName,
        pluralName: this.pluralize(entityName),
        properties: [],
        operations: [],
        hasAuth: this.hasAuthentication(operation),
        requiredRoles: this.extractRequiredRoles(operation)
      });
    }

    const entity = entityMap.get(entityName)!;
    
    const entityOperation: EntityOperation = {
      method: method as any,
      path,
      operationId: operation.operationId || `${method.toLowerCase()}${entityName}`,
      summary: operation.summary,
      description: operation.description,
      parameters: this.extractParameters(operation),
      requestBody: this.extractRequestBody(operation),
      response: this.extractResponse(operation),
      requiresAuth: this.hasAuthentication(operation),
      requiredRoles: this.extractRequiredRoles(operation)
    };

    entity.operations.push(entityOperation);

    // Extract entity properties from request/response schemas
    if (entityOperation.requestBody) {
      entity.properties = this.mergeProperties(entity.properties, entityOperation.requestBody.properties);
    }

    if (entityOperation.response.properties) {
      entity.properties = this.mergeProperties(entity.properties, entityOperation.response.properties);
    }
  }

  private sanitizeEntityName(name: string): string {
    return name.replace(/[^a-zA-Z0-9]/g, '').replace(/s$/, '');
  }

  private pluralize(name: string): string {
    if (name.endsWith('y')) {
      return name.slice(0, -1) + 'ies';
    }
    if (name.endsWith('s') || name.endsWith('sh') || name.endsWith('ch') || name.endsWith('x') || name.endsWith('z')) {
      return name + 'es';
    }
    return name + 's';
  }

  private hasAuthentication(operation: Operation): boolean {
    return !!(operation.security && operation.security.length > 0) || 
           !!(this.spec.security && this.spec.security.length > 0);
  }

  private extractRequiredRoles(operation: Operation): string[] | undefined {
    // Extract roles from security requirements or operation description
    const description = operation.description || '';
    const roleMatch = description.match(/role[s]?\s*:\s*([A-Z_,\s]+)/i);
    if (roleMatch) {
      return roleMatch[1].split(',').map(role => role.trim());
    }
    return undefined;
  }

  private extractParameters(operation: Operation): any[] {
    if (!operation.parameters) return [];
    
    return operation.parameters.map(param => ({
      name: param.name,
      type: this.getTypeFromSchema(param.schema),
      in: param.in,
      required: param.required || false,
      description: param.description
    }));
  }

  private extractRequestBody(operation: Operation): any {
    if (!operation.requestBody || !operation.requestBody.content) return undefined;

    const jsonContent = operation.requestBody.content['application/json'];
    if (!jsonContent || !jsonContent.schema) return undefined;

    return {
      type: this.getTypeFromSchema(jsonContent.schema),
      properties: this.extractPropertiesFromSchema(jsonContent.schema)
    };
  }

  private extractResponse(operation: Operation): any {
    const successResponse = operation.responses['200'] || operation.responses['201'];
    if (!successResponse || !successResponse.content) {
      return { type: 'void' };
    }

    const jsonContent = successResponse.content['application/json'];
    if (!jsonContent || !jsonContent.schema) {
      return { type: 'void' };
    }

    const schema = jsonContent.schema;
    const isArray = schema.type === 'array';
    const isPaginated = this.isPaginatedResponse(schema);

    return {
      type: this.getTypeFromSchema(schema),
      properties: this.extractPropertiesFromSchema(schema),
      isArray,
      isPaginated
    };
  }

  private isPaginatedResponse(schema: Schema): boolean {
    if (schema.properties) {
      const props = Object.keys(schema.properties);
      return props.includes('content') && props.includes('totalElements') && props.includes('totalPages');
    }
    return false;
  }

  private extractPropertiesFromSchema(schema: Schema): EntityProperty[] {
    if (!schema.properties) return [];

    return Object.entries(schema.properties).map(([name, propSchema]) => ({
      name,
      type: this.getTypeFromSchema(propSchema),
      required: schema.required?.includes(name) || false,
      format: propSchema.format,
      description: propSchema.description,
      enum: propSchema.enum,
      isId: name === 'id' || name.endsWith('Id')
    }));
  }

  private getTypeFromSchema(schema: Schema): string {
    if (schema.type === 'array' && schema.items) {
      return `${this.getTypeFromSchema(schema.items)}[]`;
    }

    if (schema.type === 'object' && schema.properties) {
      return schema.title || 'object';
    }

    if (schema.enum) {
      return schema.enum.map(v => `'${v}'`).join(' | ');
    }

    switch (schema.type) {
      case 'string':
        if (schema.format === 'date' || schema.format === 'date-time') {
          return 'Date';
        }
        return 'string';
      case 'number':
      case 'integer':
        return 'number';
      case 'boolean':
        return 'boolean';
      case 'object':
        return 'object';
      default:
        return 'any';
    }
  }

  private mergeProperties(existing: EntityProperty[], newProps: EntityProperty[]): EntityProperty[] {
    const merged = [...existing];
    
    newProps.forEach(newProp => {
      const existingIndex = merged.findIndex(p => p.name === newProp.name);
      if (existingIndex >= 0) {
        merged[existingIndex] = {
          ...merged[existingIndex],
          ...newProp,
          required: merged[existingIndex].required || newProp.required
        };
      } else {
        merged.push(newProp);
      }
    });

    return merged;
  }

  getApiInfo() {
    return {
      title: this.spec.info.title,
      version: this.spec.info.version,
      description: this.spec.info.description,
      baseUrl: this.spec.servers?.[0]?.url || ''
    };
  }

  getAuthConfig() {
    const securitySchemes = this.spec.components?.securitySchemes;
    if (!securitySchemes) {
      return { enabled: false, type: 'none' };
    }

    const jwtScheme = Object.values(securitySchemes).find(scheme => 
      scheme.type === 'http' && scheme.scheme === 'bearer'
    );

    if (jwtScheme) {
      return {
        enabled: true,
        type: 'jwt',
        loginEndpoint: this.findAuthEndpoint('login'),
        refreshEndpoint: this.findAuthEndpoint('refresh')
      };
    }

    return { enabled: false, type: 'none' };
  }

  private findAuthEndpoint(type: 'login' | 'refresh'): string | undefined {
    for (const [path, pathItem] of Object.entries(this.spec.paths)) {
      if (path.includes('auth') || path.includes('login')) {
        if (type === 'login' && pathItem.post && path.includes('login')) {
          return path;
        }
        if (type === 'refresh' && pathItem.post && path.includes('refresh')) {
          return path;
        }
      }
    }
    return undefined;
  }
}
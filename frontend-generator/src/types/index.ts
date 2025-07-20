export interface OpenAPISpec {
  openapi: string;
  info: {
    title: string;
    version: string;
    description?: string;
  };
  servers?: Array<{
    url: string;
    description?: string;
  }>;
  paths: Record<string, PathItem>;
  components?: {
    schemas?: Record<string, Schema>;
    securitySchemes?: Record<string, SecurityScheme>;
  };
  security?: SecurityRequirement[];
}

export interface PathItem {
  get?: Operation;
  post?: Operation;
  put?: Operation;
  delete?: Operation;
  patch?: Operation;
}

export interface Operation {
  tags?: string[];
  summary?: string;
  description?: string;
  operationId?: string;
  parameters?: Parameter[];
  requestBody?: RequestBody;
  responses: Record<string, Response>;
  security?: SecurityRequirement[];
}

export interface Parameter {
  name: string;
  in: 'query' | 'header' | 'path' | 'cookie';
  description?: string;
  required?: boolean;
  schema: Schema;
}

export interface RequestBody {
  description?: string;
  content: Record<string, MediaType>;
  required?: boolean;
}

export interface Response {
  description: string;
  content?: Record<string, MediaType>;
}

export interface MediaType {
  schema: Schema;
}

export interface Schema {
  type?: string;
  format?: string;
  items?: Schema;
  properties?: Record<string, Schema>;
  required?: string[];
  enum?: any[];
  allOf?: Schema[];
  oneOf?: Schema[];
  anyOf?: Schema[];
  $ref?: string;
  description?: string;
  example?: any;
  title?: string;
}

export interface SecurityScheme {
  type: string;
  scheme?: string;
  bearerFormat?: string;
  description?: string;
}

export interface SecurityRequirement {
  [key: string]: string[];
}

export interface GeneratedEntity {
  name: string;
  pluralName: string;
  properties: EntityProperty[];
  operations: EntityOperation[];
  hasAuth: boolean;
  requiredRoles?: string[];
}

export interface EntityProperty {
  name: string;
  type: string;
  required: boolean;
  format?: string;
  description?: string;
  enum?: string[];
  isId?: boolean;
}

export interface EntityOperation {
  method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
  path: string;
  operationId: string;
  summary?: string;
  description?: string;
  parameters: EntityParameter[];
  requestBody?: EntityRequestBody;
  response: EntityResponse;
  requiresAuth: boolean;
  requiredRoles?: string[];
}

export interface EntityParameter {
  name: string;
  type: string;
  in: 'query' | 'path' | 'header';
  required: boolean;
  description?: string;
}

export interface EntityRequestBody {
  type: string;
  properties: EntityProperty[];
}

export interface EntityResponse {
  type: string;
  properties?: EntityProperty[];
  isArray?: boolean;
  isPaginated?: boolean;
}

export interface GeneratorConfig {
  apiUrl: string;
  outputDir: string;
  projectName: string;
  baseUrl: string;
  authType?: 'jwt' | 'basic' | 'none';
  uiFramework?: 'material-ui' | 'ant-design' | 'chakra-ui';
  includeTests?: boolean;
  includeDocker?: boolean;
}

export interface TemplateContext {
  projectName: string;
  entities: GeneratedEntity[];
  authConfig: {
    enabled: boolean;
    type: string;
    loginEndpoint?: string;
    refreshEndpoint?: string;
  };
  apiConfig: {
    baseUrl: string;
    endpoints: Record<string, EntityOperation[]>;
  };
  uiConfig: {
    framework: string;
    theme: any;
  };
}
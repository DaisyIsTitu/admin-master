import * as fs from 'fs-extra';
import * as path from 'path';
import * as Handlebars from 'handlebars';
import { OpenAPIParser } from '../parsers/openapi-parser';
import { GeneratorConfig, TemplateContext, GeneratedEntity } from '../types';

export class FrontendGenerator {
  private config: GeneratorConfig;
  private templateDir: string;
  private outputDir: string;

  constructor(config: GeneratorConfig) {
    this.config = config;
    this.templateDir = path.resolve(__dirname, '../../templates');
    this.outputDir = path.resolve(config.outputDir);
  }

  async generate(): Promise<void> {
    console.log('🚀 Starting frontend generation...');
    
    // Parse OpenAPI specification
    const parser = await OpenAPIParser.fromUrl(this.config.apiUrl);
    const entities = parser.getEntities();
    const apiInfo = parser.getApiInfo();
    const authConfig = parser.getAuthConfig();

    // Create template context
    const context: TemplateContext = {
      projectName: this.config.projectName,
      entities,
      authConfig,
      apiConfig: {
        baseUrl: this.config.baseUrl,
        endpoints: this.groupEndpointsByEntity(entities)
      },
      uiConfig: {
        framework: this.config.uiFramework || 'material-ui',
        theme: {}
      }
    };

    // Register Handlebars helpers
    this.registerHandlebarsHelpers();

    // Ensure output directory exists
    await fs.ensureDir(this.outputDir);

    // Generate project structure
    await this.generateProjectStructure();

    // Generate package.json
    await this.generatePackageJson(context);

    // Generate configuration files
    await this.generateConfigFiles(context);

    // Generate types
    await this.generateTypes(context);

    // Generate API services
    await this.generateApiServices(context);

    // Generate components
    await this.generateComponents(context);

    // Generate pages
    await this.generatePages(context);

    // Generate main App component
    await this.generateApp(context);

    // Generate index.html and main.tsx
    await this.generateEntryFiles(context);

    console.log('✅ Frontend generation completed successfully!');
    console.log(`📁 Output directory: ${this.outputDir}`);
  }

  private groupEndpointsByEntity(entities: GeneratedEntity[]): Record<string, any[]> {
    const grouped: Record<string, any[]> = {};
    entities.forEach(entity => {
      grouped[entity.name] = entity.operations;
    });
    return grouped;
  }

  private registerHandlebarsHelpers(): void {
    Handlebars.registerHelper('camelCase', (str: string) => {
      return str.charAt(0).toLowerCase() + str.slice(1);
    });

    Handlebars.registerHelper('pascalCase', (str: string) => {
      return str.charAt(0).toUpperCase() + str.slice(1);
    });

    Handlebars.registerHelper('kebabCase', (str: string) => {
      return str.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
    });

    Handlebars.registerHelper('eq', (a: any, b: any) => a === b);

    Handlebars.registerHelper('or', (a: any, b: any) => a || b);

    Handlebars.registerHelper('and', (a: any, b: any) => a && b);

    Handlebars.registerHelper('json', (obj: any) => JSON.stringify(obj, null, 2));

    Handlebars.registerHelper('ifEquals', function(this: any, arg1: any, arg2: any, options: any) {
      return (arg1 === arg2) ? options.fn(this) : options.inverse(this);
    });
  }

  private async generateProjectStructure(): Promise<void> {
    const dirs = [
      'src',
      'src/components',
      'src/components/common',
      'src/components/forms',
      'src/components/tables',
      'src/pages',
      'src/services',
      'src/types',
      'src/hooks',
      'src/utils',
      'src/context',
      'public'
    ];

    for (const dir of dirs) {
      await fs.ensureDir(path.join(this.outputDir, dir));
    }
  }

  private async generatePackageJson(context: TemplateContext): Promise<void> {
    const template = await fs.readFile(path.join(this.templateDir, 'package.json.hbs'), 'utf8');
    const compiled = Handlebars.compile(template);
    const content = compiled(context);
    
    await fs.writeFile(path.join(this.outputDir, 'package.json'), content);
  }

  private async generateConfigFiles(context: TemplateContext): Promise<void> {
    const configs = [
      'tsconfig.json.hbs',
      'vite.config.ts.hbs',
      'index.html.hbs',
      '.gitignore.hbs'
    ];

    for (const configFile of configs) {
      const template = await fs.readFile(path.join(this.templateDir, 'config', configFile), 'utf8');
      const compiled = Handlebars.compile(template);
      const content = compiled(context);
      
      const outputFile = configFile.replace('.hbs', '');
      await fs.writeFile(path.join(this.outputDir, outputFile), content);
    }
  }

  private async generateTypes(context: TemplateContext): Promise<void> {
    // Generate API types
    const apiTypesTemplate = await fs.readFile(path.join(this.templateDir, 'types', 'api.ts.hbs'), 'utf8');
    const apiTypesCompiled = Handlebars.compile(apiTypesTemplate);
    const apiTypesContent = apiTypesCompiled(context);
    await fs.writeFile(path.join(this.outputDir, 'src/types/api.ts'), apiTypesContent);

    // Generate common types
    const commonTypesTemplate = await fs.readFile(path.join(this.templateDir, 'types', 'common.ts.hbs'), 'utf8');
    const commonTypesCompiled = Handlebars.compile(commonTypesTemplate);
    const commonTypesContent = commonTypesCompiled(context);
    await fs.writeFile(path.join(this.outputDir, 'src/types/common.ts'), commonTypesContent);
  }

  private async generateApiServices(context: TemplateContext): Promise<void> {
    // Generate main API client
    const apiClientTemplate = await fs.readFile(path.join(this.templateDir, 'services', 'api-client.ts.hbs'), 'utf8');
    const apiClientCompiled = Handlebars.compile(apiClientTemplate);
    const apiClientContent = apiClientCompiled(context);
    await fs.writeFile(path.join(this.outputDir, 'src/services/api-client.ts'), apiClientContent);

    // Generate auth service
    if (context.authConfig.enabled) {
      const authServiceTemplate = await fs.readFile(path.join(this.templateDir, 'services', 'auth.ts.hbs'), 'utf8');
      const authServiceCompiled = Handlebars.compile(authServiceTemplate);
      const authServiceContent = authServiceCompiled(context);
      await fs.writeFile(path.join(this.outputDir, 'src/services/auth.ts'), authServiceContent);
    }

    // Generate entity services
    for (const entity of context.entities) {
      const entityServiceTemplate = await fs.readFile(path.join(this.templateDir, 'services', 'entity.ts.hbs'), 'utf8');
      const entityServiceCompiled = Handlebars.compile(entityServiceTemplate);
      const entityServiceContent = entityServiceCompiled({ ...context, entity });
      await fs.writeFile(path.join(this.outputDir, `src/services/${entity.name.toLowerCase()}.ts`), entityServiceContent);
    }
  }

  private async generateComponents(context: TemplateContext): Promise<void> {
    // Generate common components
    const commonComponents = ['Layout.tsx.hbs', 'DataTable.tsx.hbs', 'FormField.tsx.hbs'];
    for (const componentFile of commonComponents) {
      const template = await fs.readFile(path.join(this.templateDir, 'components', componentFile), 'utf8');
      const compiled = Handlebars.compile(template);
      const content = compiled(context);
      
      const outputFile = componentFile.replace('.hbs', '');
      await fs.writeFile(path.join(this.outputDir, 'src/components/common', outputFile), content);
    }

    // Generate entity-specific components
    for (const entity of context.entities) {
      const entityComponents = ['Table.tsx.hbs', 'Form.tsx.hbs', 'Details.tsx.hbs'];
      
      for (const componentFile of entityComponents) {
        const template = await fs.readFile(path.join(this.templateDir, 'components', componentFile), 'utf8');
        const compiled = Handlebars.compile(template);
        const content = compiled({ ...context, entity });
        
        const outputFile = `${entity.name}${componentFile.replace('.tsx.hbs', '.tsx')}`;
        await fs.writeFile(path.join(this.outputDir, 'src/components', outputFile), content);
      }
    }
  }

  private async generatePages(context: TemplateContext): Promise<void> {
    // Generate Dashboard page
    const dashboardTemplate = await fs.readFile(path.join(this.templateDir, 'pages', 'Dashboard.tsx.hbs'), 'utf8');
    const dashboardCompiled = Handlebars.compile(dashboardTemplate);
    const dashboardContent = dashboardCompiled(context);
    await fs.writeFile(path.join(this.outputDir, 'src/pages/Dashboard.tsx'), dashboardContent);

    // Generate Login page if auth is enabled
    if (context.authConfig.enabled) {
      const loginTemplate = await fs.readFile(path.join(this.templateDir, 'pages', 'Login.tsx.hbs'), 'utf8');
      const loginCompiled = Handlebars.compile(loginTemplate);
      const loginContent = loginCompiled(context);
      await fs.writeFile(path.join(this.outputDir, 'src/pages/Login.tsx'), loginContent);
    }

    // Generate entity pages
    for (const entity of context.entities) {
      const entityPageTemplate = await fs.readFile(path.join(this.templateDir, 'pages', 'EntityList.tsx.hbs'), 'utf8');
      const entityPageCompiled = Handlebars.compile(entityPageTemplate);
      const entityPageContent = entityPageCompiled({ ...context, entity });
      await fs.writeFile(path.join(this.outputDir, `src/pages/${entity.name}List.tsx`), entityPageContent);
    }
  }

  private async generateApp(context: TemplateContext): Promise<void> {
    const appTemplate = await fs.readFile(path.join(this.templateDir, 'App.tsx.hbs'), 'utf8');
    const appCompiled = Handlebars.compile(appTemplate);
    const appContent = appCompiled(context);
    await fs.writeFile(path.join(this.outputDir, 'src/App.tsx'), appContent);
  }

  private async generateEntryFiles(context: TemplateContext): Promise<void> {
    // Generate main.tsx
    const mainTemplate = await fs.readFile(path.join(this.templateDir, 'main.tsx.hbs'), 'utf8');
    const mainCompiled = Handlebars.compile(mainTemplate);
    const mainContent = mainCompiled(context);
    await fs.writeFile(path.join(this.outputDir, 'src/main.tsx'), mainContent);
  }
}
#!/usr/bin/env node

import { Command } from 'commander';
import * as inquirer from 'inquirer';
import * as path from 'path';
import * as fs from 'fs-extra';
import chalk from 'chalk';
import * as ora from 'ora';
import { FrontendGenerator } from './generators/frontend-generator';
import { GeneratorConfig } from './types';

const program = new Command();

program
  .name('admin-generator')
  .description('Generate React admin dashboards from OpenAPI specifications')
  .version('1.0.0');

program
  .command('generate')
  .description('Generate admin frontend from OpenAPI spec')
  .option('-u, --url <url>', 'OpenAPI specification URL')
  .option('-f, --file <file>', 'OpenAPI specification file path')
  .option('-o, --output <dir>', 'Output directory', './generated-frontend')
  .option('-n, --name <name>', 'Project name', 'admin-dashboard')
  .option('-b, --base-url <url>', 'API base URL', '')
  .option('--ui-framework <framework>', 'UI framework (material-ui, ant-design, chakra-ui)', 'material-ui')
  .option('--no-tests', 'Skip test generation')
  .option('--no-docker', 'Skip Docker configuration')
  .option('--interactive', 'Run in interactive mode')
  .action(async (options) => {
    try {
      let config: GeneratorConfig;

      if (options.interactive) {
        config = await runInteractiveMode();
      } else {
        config = await createConfigFromOptions(options);
      }

      const spinner = ora('Generating admin frontend...').start();

      try {
        const generator = new FrontendGenerator(config);
        await generator.generate();
        
        spinner.succeed('Frontend generated successfully!');
        
        console.log('\\n' + chalk.green('✅ Generation completed!'));
        console.log(chalk.cyan('📁 Output directory: ') + config.outputDir);
        console.log('\\n' + chalk.yellow('Next steps:'));
        console.log('1. cd ' + config.outputDir);
        console.log('2. npm install');
        console.log('3. npm run dev');
        
      } catch (error) {
        spinner.fail('Generation failed');
        console.error(chalk.red('Error: ') + error);
        process.exit(1);
      }
    } catch (error) {
      console.error(chalk.red('Error: ') + error);
      process.exit(1);
    }
  });

async function runInteractiveMode(): Promise<GeneratorConfig> {
  console.log(chalk.blue.bold('🚀 Admin Dashboard Generator'));
  console.log(chalk.gray('Interactive mode - answer the following questions:\\n'));

  const answers = await inquirer.prompt([
    {
      type: 'input',
      name: 'apiUrl',
      message: 'OpenAPI specification URL:',
      default: 'http://localhost:8080/api-docs',
      validate: (input: string) => {
        if (!input.trim()) return 'URL is required';
        try {
          new URL(input);
          return true;
        } catch {
          return 'Please enter a valid URL';
        }
      }
    },
    {
      type: 'input',
      name: 'projectName',
      message: 'Project name:',
      default: 'admin-dashboard',
      validate: (input: string) => input.trim() ? true : 'Project name is required'
    },
    {
      type: 'input',
      name: 'outputDir',
      message: 'Output directory:',
      default: './generated-frontend',
      validate: (input: string) => input.trim() ? true : 'Output directory is required'
    },
    {
      type: 'input',
      name: 'baseUrl',
      message: 'API base URL (leave empty to use the same server):',
      default: ''
    },
    {
      type: 'list',
      name: 'uiFramework',
      message: 'UI Framework:',
      choices: [
        { name: 'Material-UI', value: 'material-ui' },
        { name: 'Ant Design', value: 'ant-design' },
        { name: 'Chakra UI', value: 'chakra-ui' }
      ],
      default: 'material-ui'
    },
    {
      type: 'confirm',
      name: 'includeTests',
      message: 'Include test files?',
      default: true
    },
    {
      type: 'confirm',
      name: 'includeDocker',
      message: 'Include Docker configuration?',
      default: true
    }
  ]);

  return {
    apiUrl: answers.apiUrl,
    outputDir: path.resolve(answers.outputDir),
    projectName: answers.projectName,
    baseUrl: answers.baseUrl,
    uiFramework: answers.uiFramework,
    includeTests: answers.includeTests,
    includeDocker: answers.includeDocker
  };
}

async function createConfigFromOptions(options: any): Promise<GeneratorConfig> {
  if (!options.url && !options.file) {
    throw new Error('Either --url or --file must be specified');
  }

  const apiUrl = options.url || `file://${path.resolve(options.file)}`;
  
  return {
    apiUrl,
    outputDir: path.resolve(options.output),
    projectName: options.name,
    baseUrl: options.baseUrl,
    uiFramework: options.uiFramework,
    includeTests: !options.noTests,
    includeDocker: !options.noDocker
  };
}

program.parse();
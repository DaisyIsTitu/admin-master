import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
    id("com.github.node-gradle.node") version "7.0.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // OpenAPI/Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // Database
    runtimeOnly("com.h2database:h2")
    
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Node.js configuration
node {
    version = "20.10.0"
    npmVersion = "10.2.3"
    download = true
    workDir = file("${project.projectDir}/.gradle/nodejs")
    npmWorkDir = file("${project.projectDir}/.gradle/npm")
}

// Frontend paths
val frontendGeneratorDir = file("${project.rootDir}/frontend-generator")
val generatedFrontendDir = file("${project.rootDir}/generated-frontend")
val staticResourcesDir = file("${project.projectDir}/src/main/resources/static")

// Task to install frontend generator dependencies
val installFrontendGenerator by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
    workingDir = frontendGeneratorDir
    args = listOf("install")
    inputs.file("${frontendGeneratorDir}/package.json")
    outputs.dir("${frontendGeneratorDir}/node_modules")
}

// Task to build frontend generator
val buildFrontendGenerator by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
    dependsOn(installFrontendGenerator)
    workingDir = frontendGeneratorDir
    args = listOf("run", "build")
    inputs.dir("${frontendGeneratorDir}/src")
    inputs.file("${frontendGeneratorDir}/tsconfig.json")
    outputs.dir("${frontendGeneratorDir}/dist")
}

// Task to generate frontend code
val generateFrontend by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
    dependsOn(buildFrontendGenerator)
    workingDir = frontendGeneratorDir
    args = listOf("run", "generate", "--", "--url", "http://localhost:8080/api-docs", "--output", generatedFrontendDir.absolutePath, "--name", "admin-dashboard")
    inputs.dir("${frontendGeneratorDir}/dist")
    inputs.dir("${frontendGeneratorDir}/templates")
    outputs.dir(generatedFrontendDir)
    
    doFirst {
        // Clean previous generated frontend
        if (generatedFrontendDir.exists()) {
            generatedFrontendDir.deleteRecursively()
        }
    }
}

// Task to install frontend dependencies
val installFrontend by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
    dependsOn(generateFrontend)
    workingDir = generatedFrontendDir
    args = listOf("install")
    inputs.file("${generatedFrontendDir}/package.json")
    outputs.dir("${generatedFrontendDir}/node_modules")
}

// Task to build frontend
val buildFrontend by tasks.registering(com.github.gradle.node.npm.task.NpmTask::class) {
    dependsOn(installFrontend)
    workingDir = generatedFrontendDir
    args = listOf("run", "build")
    inputs.dir("${generatedFrontendDir}/src")
    inputs.file("${generatedFrontendDir}/package.json")
    inputs.file("${generatedFrontendDir}/vite.config.ts")
    outputs.dir("${generatedFrontendDir}/dist")
}

// Task to copy frontend build to static resources
val copyFrontendToResources by tasks.registering(Copy::class) {
    dependsOn(buildFrontend)
    from("${generatedFrontendDir}/dist")
    into(staticResourcesDir)
    
    doFirst {
        // Clean previous static resources
        if (staticResourcesDir.exists()) {
            staticResourcesDir.deleteRecursively()
        }
        staticResourcesDir.mkdirs()
    }
}

// Make processResources depend on frontend build
tasks.named("processResources") {
    dependsOn(copyFrontendToResources)
}

// Task to generate and build everything
val buildFullstack by tasks.registering {
    dependsOn(copyFrontendToResources)
    dependsOn("build")
    description = "Build both frontend and backend"
}

// Task to run the application with frontend generation
val runFullstack by tasks.registering {
    dependsOn(buildFullstack)
    dependsOn("bootRun")
    description = "Generate frontend and run the full application"
}
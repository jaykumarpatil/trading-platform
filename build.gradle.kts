plugins {
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("com.github.node-gradle.node") version "7.0.1" apply false
    id("org.hidetake.python") version "2.13.1" apply false
    java
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    group = "se.ta"
    version = "1.0.0-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.0"))
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.0"))
        
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

allprojects {
    group = "com.tradingplatform"
    version = "1.0.0"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// Configuration for all Java projects
configure(subprojects.filter { it.path.startsWith(":microservices") || it.path in listOf(":api", ":auth", ":common", ":util") }) {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    tasks.withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    dependencies {
        "implementation"("org.springframework.boot:spring-boot-starter-web")
        "implementation"("org.springframework.boot:spring-boot-starter-data-jpa")
        "implementation"("org.springframework.kafka:spring-kafka")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
    }
}

// Configuration for Node.js projects
configure(subprojects.filter { it.path.startsWith(":node-services") }) {
    apply(plugin = "com.github.node-gradle.node")

    tasks.register("npmBuild") {
        doLast {
            exec {
                workingDir(projectDir)
                commandLine("npm", "run", "build")
            }
        }
    }

    tasks.register("npmInstall") {
        doLast {
            exec {
                workingDir(projectDir)
                commandLine("npm", "install")
            }
        }
    }
}

// Configuration for Python projects
configure(subprojects.filter { it.path.startsWith(":services:python") }) {
    tasks.register("pythonSetup") {
        doLast {
            exec {
                workingDir(projectDir)
                commandLine("python3", "-m", "venv", "venv")
            }
            exec {
                workingDir(projectDir)
                commandLine("./venv/bin/pip", "install", "-r", "requirements.txt")
            }
        }
    }

    tasks.register("pythonBuild") {
        dependsOn("pythonSetup")
    }
}

// Configuration for Frontend Angular project
project(":frontend") {
    apply(plugin = "com.github.node-gradle.node")

    tasks.register("npmBuild") {
        doLast {
            exec {
                workingDir(projectDir)
                commandLine("npm", "run", "build")
            }
        }
    }

    tasks.register("npmInstall") {
        doLast {
            exec {
                workingDir(projectDir)
                commandLine("npm", "install")
            }
        }
    }
}

// Task to build everything
tasks.register("buildAll") {
    dependsOn(subprojects.filter { it.path.startsWith(":microservices") }.map { it.tasks.named("build") })
    dependsOn(subprojects.filter { it.path.startsWith(":node-services") }.map { it.tasks.named("npmBuild") })
    dependsOn(subprojects.filter { it.path.startsWith(":python-services") }.map { it.tasks.named("pythonBuild") })
    dependsOn(":frontend:npmBuild")
    
    // Ensure proper order of execution
    doFirst {
        // Install dependencies first
        subprojects.filter { it.path.startsWith(":node-services") }.forEach { it.tasks.named("npmInstall").get().execute() }
        project(":frontend").tasks.named("npmInstall").get().execute()
    }
}

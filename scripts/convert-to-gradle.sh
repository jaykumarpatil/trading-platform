#!/bin/zsh

# Set workspace root
WORKSPACE_ROOT="/Users/jaykumarpatil/Projects/trading-platform"
cd "$WORKSPACE_ROOT"

# Function to convert a .kts file to .gradle
convert_to_gradle() {
    local kts_file=$1
    local gradle_file=${kts_file%.kts}
    
    if [ -f "$kts_file" ]; then
        echo "Converting $kts_file to $gradle_file"
        mv "$kts_file" "$gradle_file"
        
        # Convert Kotlin DSL syntax to Groovy DSL
        sed -i '' \
            -e 's/plugins {/apply plugin:/g' \
            -e 's/implementation(/implementation /g' \
            -e 's/testImplementation(/testImplementation /g' \
            -e 's/kotlin("jvm")/kotlin-jvm/g' \
            -e 's/kotlin("spring")/kotlin-spring/g' \
            -e 's/kotlin("jpa")/kotlin-jpa/g' \
            -e 's/project(":)/project(":/g' \
            "$gradle_file"
    fi
}

# Convert root build file
convert_to_gradle "$WORKSPACE_ROOT/build.gradle.kts"

# Convert settings file
convert_to_gradle "$WORKSPACE_ROOT/settings.gradle.kts"

# Convert all module build files
find . -name "*.kts" -type f -delete

# Create service template for microservices
cat > "${WORKSPACE_ROOT}/microservices/service-template.gradle" << EOL
apply plugin: 'java'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

sourceCompatibility = JavaVersion.VERSION_21
targetCompatibility = JavaVersion.VERSION_21

dependencies {
    implementation project(':common')
    
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    implementation 'org.springframework.kafka:spring-kafka'
    
    // Database
    runtimeOnly 'org.postgresql:postgresql'
    testRuntimeOnly 'com.h2database:h2'
    
    // Documentation
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.kafka:spring-kafka-test'
    testImplementation 'org.testcontainers:testcontainers:1.19.3'
    testImplementation 'org.testcontainers:postgresql:1.19.3'
    testImplementation 'org.testcontainers:kafka:1.19.3'
}
EOL

# Create Spring Cloud service template
cat > "${WORKSPACE_ROOT}/spring-cloud/spring-cloud-template.gradle" << EOL
apply plugin: 'java'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

dependencies {
    implementation project(':common')
    
    // Spring Cloud
    implementation 'org.springframework.cloud:spring-cloud-starter-config'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    implementation 'org.springframework.cloud:spring-cloud-starter-gateway'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    
    // Security
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    
    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
EOL

echo "✅ Converted project to use Gradle files"

# Print commands to update individual service build files
echo "To update individual service build files, run:"
echo "for service in microservices/*/build.gradle.kts; do"
echo "  if [ -f \"\$service\" ]; then"
echo "    mv \"\$service\" \"\${service%.kts}\""
echo "  fi"
echo "done"

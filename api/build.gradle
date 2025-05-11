plugins {
    id("java-library")
    id("io.spring.dependency-management")
}

dependencies {
    api(project(":util"))
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.cloud:spring-cloud-starter-stream-kafka")
    api("org.springframework.cloud:spring-cloud-starter-openfeign")
    
    implementation("io.swagger.core.v3:swagger-annotations:2.2.20")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-support")
}

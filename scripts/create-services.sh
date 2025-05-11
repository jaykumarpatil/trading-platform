# Create basic service files
createBasicService() {
    local servicePath=$1
    local serviceName=$2
    local packageBase="com.tradingplatform"
    local packagePath="${packageBase}.${serviceName}"
    local mainPath="${servicePath}/src/main/java/${packagePath//.//}"
    local testPath="${servicePath}/src/test/java/${packagePath//.//}"
    local resourcePath="${servicePath}/src/main/resources"
    local testResourcePath="${servicePath}/src/test/resources"

    # Create directory structure
    mkdir -p "${mainPath}"/{config,controller,service/impl,repository,model,exception}
    mkdir -p "${testPath}"/{config,controller,service,repository}
    mkdir -p "${resourcePath}"
    mkdir -p "${testResourcePath}"

    # Create application.yml if it doesn't exist
    if [[ ! -f "${resourcePath}/application.yml" ]]; then
        cat > "${resourcePath}/application.yml" << EOL
spring:
  application:
    name: ${serviceName}
  datasource:
    url: jdbc:postgresql://localhost:5432/${serviceName}
    username: \${POSTGRES_USER:postgres}
    password: \${POSTGRES_PASSWORD:postgres}
  jpa:
    hibernate:
      ddl-auto: update
  kafka:
    bootstrap-servers: \${KAFKA_SERVERS:localhost:9092}
    consumer:
      group-id: ${serviceName}
      auto-offset-reset: earliest
  cloud:
    config:
      enabled: true
      uri: \${CONFIG_SERVER_URL:http://localhost:8888}

server:
  port: 0

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
  metrics:
    tags:
      application: ${serviceName}

eureka:
  client:
    serviceUrl:
      defaultZone: \${EUREKA_SERVER:http://localhost:8761/eureka}
  instance:
    preferIpAddress: true
EOL
    fi

    # Create test application.yml
    if [[ ! -f "${testResourcePath}/application.yml" ]]; then
        cat > "${testResourcePath}/application.yml" << EOL
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    username: sa
    password: sa
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
  cloud:
    config:
      enabled: false
  kafka:
    bootstrap-servers: \${spring.embedded.kafka.brokers}
EOL
    fi

    # Create logback configurations
    if [[ ! -f "${resourcePath}/logback-spring.xml" ]]; then
        cat > "${resourcePath}/logback-spring.xml" << EOL
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <springProperty scope="context" name="appName" source="spring.application.name"/>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>

    <logger name="com.tradingplatform" level="DEBUG"/>
</configuration>
EOL
    fi
}

# Create all required service directories
services=(
    "admin"
    "order"
    "portfolio"
    "product"
    "realtime-data"
    "recommendation"
    "review"
    "tradinganalytics"
    "userauth"
    "websocket"
)

for service in "${services[@]}"; do
    createBasicService "/Users/jaykumarpatil/Projects/trading-platform/microservices/${service}-service" "${service}"
done

# Create Spring Cloud services
cloudServices=(
    "authorization-server"
    "config-server"
    "eureka-server"
    "gateway"
)

for service in "${cloudServices[@]}"; do
    createBasicService "/Users/jaykumarpatil/Projects/trading-platform/spring-cloud/${service}" "${service#*-}"
done

# Create engine services
createBasicService "/Users/jaykumarpatil/Projects/trading-platform/engine/analytics-engine" "analytics"

echo "✅ Service structures created successfully"

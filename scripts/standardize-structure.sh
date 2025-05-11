#!/bin/zsh

# Set workspace root
WORKSPACE_ROOT="/Users/jaykumarpatil/Projects/trading-platform"
cd "$WORKSPACE_ROOT"

# Function to create standard service structure
create_service_structure() {
    local SERVICE_PATH=$1
    local SERVICE_NAME=$2
    local PACKAGE_PATH="com/tradingplatform/${SERVICE_NAME}"
    
    # Create main directories
    mkdir -p "$SERVICE_PATH/src/main/java/${PACKAGE_PATH}"/{config,controller,service/impl,repository,entity,dto}
    mkdir -p "$SERVICE_PATH/src/main/resources"
    mkdir -p "$SERVICE_PATH/src/test/java/${PACKAGE_PATH}"/{controller,service}
    mkdir -p "$SERVICE_PATH/src/test/resources"
    
    # Create basic application.yml
    cat > "$SERVICE_PATH/src/main/resources/application.yml" << EOF
spring:
  application:
    name: ${SERVICE_NAME}
  datasource:
    url: jdbc:postgresql://localhost:5432/${SERVICE_NAME}
    username: \${POSTGRES_USER:postgres}
    password: \${POSTGRES_PASSWORD:postgres}
  jpa:
    hibernate:
      ddl-auto: update
  kafka:
    bootstrap-servers: \${KAFKA_SERVERS:localhost:9092}
    
server:
  port: 0

eureka:
  client:
    serviceUrl:
      defaultZone: \${EUREKA_SERVER:http://localhost:8761/eureka}
EOF

    # Create test application.yml
    cat > "$SERVICE_PATH/src/test/resources/application.yml" << EOF
spring:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    username: sa
    password: sa
EOF
}

# Create microservice structures
for service in admin order portfolio product realtime-data recommendation review tradinganalytics userauth websocket; do
    create_service_structure "$WORKSPACE_ROOT/microservices/${service}-service" "$service"
done

# Create infrastructure service structures
for service in authorization eureka gateway; do
    create_service_structure "$WORKSPACE_ROOT/spring-cloud/${service}-server" "$service"
done

# Create analytics engine structure
create_service_structure "$WORKSPACE_ROOT/engine/analytics-engine" "analytics"

# Update build.gradle.kts files where needed
for service in microservices/*/build.gradle.kts spring-cloud/*/build.gradle.kts engine/*/build.gradle.kts; do
    if [ ! -f "$service" ]; then
        cp "$WORKSPACE_ROOT/microservices/service-template.gradle.kts" "$service"
    fi
done

# Generate updated folder structure
tree -I 'node_modules|dist|target|build|.git|.gradle|.idea|.vscode' \
     --charset UTF-8 \
     --dirsfirst \
     -F \
     > folder-structure.txt

echo "✅ Project structure has been standardized"

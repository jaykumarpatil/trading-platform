rootProject.name = "trading-platform"

// Core modules
include(":api")
include(":common")
include(":util")

// Microservices
include(":microservices:admin-service")
include(":microservices:order-service")
include(":microservices:portfolio-service")
include(":microservices:product-composite-service")
include(":microservices:product-service")
include(":microservices:recommendation-service")
include(":microservices:realtime-data-service")
include(":microservices:reporting-service")
include(":microservices:review-service")
include(":microservices:tradinganalytics-service")
include(":microservices:userauth-service")

// Spring Cloud Services
include(":spring-cloud:authorization-server")
include(":spring-cloud:eureka-server")
include(":spring-cloud:gateway")

// Engine modules
include(":engine:analytics")
include(":engine:trading")

// Spring Cloud components
include(":spring-cloud:authorization-server")
include(":spring-cloud:eureka-server")
include(":spring-cloud:gateway")

// Frontend
include(":frontend")

// Project directory mapping
project(":api").projectDir = file("backend-java/api")
project(":auth").projectDir = file("backend-java/auth")
project(":common").projectDir = file("backend-java/common")
project(":util").projectDir = file("backend-java/util")

// Microservices mapping
project(":microservices:admin-service").projectDir = file("backend-java/microservices/admin-service")
project(":microservices:order-service").projectDir = file("backend-java/microservices/order-service")
project(":microservices:portfolio-service").projectDir = file("backend-java/microservices/portfolio-service")
project(":microservices:realtimedata-service").projectDir = file("backend-java/microservices/realtimedata-service")
project(":microservices:reporting-service").projectDir = file("backend-java/microservices/reporting-service")
project(":microservices:tradinganalytics-service").projectDir = file("backend-java/microservices/tradinganalytics-service")
project(":microservices:userauth-service").projectDir = file("backend-java/microservices/userauth-service")

// Node.js services mapping
project(":node-services:gateway-api").projectDir = file("backend-nodejs/gateway-api")
project(":node-services:websocket-service").projectDir = file("backend-nodejs/websocket-service")

// Python services mapping
project(":python-services:analytics-service").projectDir = file("backend-python/analytics-service")
project(":python-services:trading-engine").projectDir = file("backend-python/trading-engine")

// Frontend mapping
project(":frontend").projectDir = file("frontend-angular")

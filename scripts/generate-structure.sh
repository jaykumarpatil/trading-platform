#!/bin/bash

# Script to generate the trading platform folder structure
# This script creates directories and files while preserving existing content

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to create a directory if it doesn't exist
create_dir() {
    if [ ! -d "$1" ]; then
        mkdir -p "$1"
        echo -e "${GREEN}Created directory:${NC} $1"
    else
        echo -e "${YELLOW}Directory already exists:${NC} $1"
    fi
}

# Function to create a file with content if it doesn't exist
create_file() {
    if [ ! -f "$1" ]; then
        # Create parent directory if it doesn't exist
        parent_dir=$(dirname "$1")
        create_dir "$parent_dir"
        
        # Create the file with placeholder content
        touch "$1"
        echo -e "${GREEN}Created file:${NC} $1"
        
        # Add basic placeholder content based on file extension
        if [[ "$1" == *.java ]]; then
            package_path=$(echo "$1" | grep -o 'com/tradingplatform.*' | sed 's/\//./g' | sed 's/\.java$//')
            class_name=$(basename "$1" .java)
            echo "package $package_path;" > "$1"
            echo "" >> "$1"
            echo "public class $class_name {" >> "$1"
            echo "    // TODO: Implement $class_name" >> "$1"
            echo "}" >> "$1"
        elif [[ "$1" == *.ts ]]; then
            echo "// TypeScript file: $(basename "$1")" > "$1"
            echo "// TODO: Implement functionality" >> "$1"
        elif [[ "$1" == *.html ]]; then
            echo "<!DOCTYPE html>" > "$1"
            echo "<html>" >> "$1"
            echo "<head>" >> "$1"
            echo "    <title>$(basename "$1" .html)</title>" >> "$1"
            echo "</head>" >> "$1"
            echo "<body>" >> "$1"
            echo "    <!-- TODO: Implement $(basename "$1" .html) -->" >> "$1"
            echo "</body>" >> "$1"
            echo "</html>" >> "$1"
        elif [[ "$1" == *.scss ]]; then
            echo "// SCSS file: $(basename "$1")" > "$1"
            echo "// TODO: Add styles" >> "$1"
        elif [[ "$1" == *.yml || "$1" == *.yaml ]]; then
            echo "# YAML configuration file: $(basename "$1")" > "$1"
            echo "# TODO: Add configuration" >> "$1"
        elif [[ "$1" == *.json ]]; then
            echo "{" > "$1"
            echo "  \"name\": \"$(basename "$1" .json)\"," >> "$1"
            echo "  \"version\": \"1.0.0\"," >> "$1"
            echo "  \"description\": \"TODO: Add description\"" >> "$1"
            echo "}" >> "$1"
        elif [[ "$1" == *.md ]]; then
            echo "# $(basename "$1" .md)" > "$1"
            echo "" >> "$1"
            echo "TODO: Add content" >> "$1"
        elif [[ "$1" == *.gradle || "$1" == *.gradle.kts ]]; then
            echo "// Gradle build file" > "$1"
            echo "plugins {" >> "$1"
            echo "    id 'java'" >> "$1"
            echo "    id 'org.springframework.boot' version '2.7.0'" >> "$1"
            echo "    id 'io.spring.dependency-management' version '1.0.11.RELEASE'" >> "$1"
            echo "}" >> "$1"
            echo "" >> "$1"
            echo "// TODO: Add dependencies and configuration" >> "$1"
        elif [[ "$1" == *.properties ]]; then
            echo "# Properties file: $(basename "$1")" > "$1"
            echo "# TODO: Add properties" >> "$1"
        elif [[ "$1" == *.py ]]; then
            echo "#!/usr/bin/env python3" > "$1"
            echo "# -*- coding: utf-8 -*-" >> "$1"
            echo "" >> "$1"
            echo "\"\"\"" >> "$1"
            echo "$(basename "$1" .py)" >> "$1"
            echo "\"\"\"" >> "$1"
            echo "" >> "$1"
            echo "def main():" >> "$1"
            echo "    # TODO: Implement functionality" >> "$1"
            echo "    pass" >> "$1"
            echo "" >> "$1"
            echo "if __name__ == '__main__':" >> "$1"
            echo "    main()" >> "$1"
        fi
    else
        echo -e "${YELLOW}File already exists:${NC} $1"
    fi
}

# Function to create a basic index.html file
create_index_html() {
    if [ ! -f "$1" ]; then
        create_file "$1"
        
        cat > "$1" << EOF
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trading Platform</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div id="app">Loading...</div>
    <script src="main.js"></script>
</body>
</html>
EOF
        echo -e "${GREEN}Created index.html with basic structure:${NC} $1"
    else
        echo -e "${YELLOW}index.html already exists:${NC} $1"
    fi
}

# Main script execution starts here
echo "Starting folder structure generation..."

# Create high-level directories
create_dir "api"
create_dir "auth0"
create_dir "common"
create_dir "docker"
create_dir "docs"
create_dir "engine"
create_dir "frontend"
create_dir "gradle"
create_dir "microservices"
create_dir "spring-cloud"
create_dir "util"

# Create api directory structure
create_dir "api/src/main/java/com/tradingplatform/api"
create_dir "api/src/main/java/com/tradingplatform/api/composite/product"
create_dir "api/src/main/java/com/tradingplatform/api/composite/trading"
create_dir "api/src/main/java/com/tradingplatform/api/core/market"
create_dir "api/src/main/java/com/tradingplatform/api/core/order"
create_dir "api/src/main/java/com/tradingplatform/api/core/portfolio"
create_dir "api/src/main/java/com/tradingplatform/api/core/product"
create_dir "api/src/main/java/com/tradingplatform/api/core/recommendation"
create_dir "api/src/main/java/com/tradingplatform/api/core/review"
create_dir "api/src/main/java/com/tradingplatform/api/event"
create_dir "api/src/main/java/com/tradingplatform/api/event/models"
create_dir "api/src/main/java/com/tradingplatform/api/exceptions"
create_dir "api/src/main/java/com/tradingplatform/api/config"

# Create API Java files
create_file "api/build.gradle.kts"
create_file "api/src/main/java/com/tradingplatform/api/ApiApplication.java"
create_file "api/src/main/java/com/tradingplatform/api/config/AsyncConfig.java"
create_file "api/src/main/java/com/tradingplatform/api/config/JacksonConfig.java"
create_file "api/src/main/java/com/tradingplatform/api/config/SecurityConfig.java"
create_file "api/src/main/java/com/tradingplatform/api/config/WebClientConfig.java"

# Create API composite product files
create_file "api/src/main/java/com/tradingplatform/api/composite/product/ProductCompositeApi.java"
create_file "api/src/main/java/com/tradingplatform/api/composite/product/ProductCompositeController.java"

# Create API composite trading files
create_file "api/src/main/java/com/tradingplatform/api/composite/trading/TradingCompositeApi.java"
create_file "api/src/main/java/com/tradingplatform/api/composite/trading/TradingCompositeController.java"

# Create API core market files
create_file "api/src/main/java/com/tradingplatform/api/core/market/MarketDataApi.java"
create_file "api/src/main/java/com/tradingplatform/api/core/market/MarketDataController.java"

# Create API core order files
create_file "api/src/main/java/com/tradingplatform/api/core/order/OrderApi.java"
create_file "api/src/main/java/com/tradingplatform/api/core/order/OrderController.java"

# Create API core portfolio files
create_file "api/src/main/java/com/tradingplatform/api/core/portfolio/PortfolioApi.java"
create_file "api/src/main/java/com/tradingplatform/api/core/portfolio/PortfolioController.java"

# Create API core product files
create_file "api/src/main/java/com/tradingplatform/api/core/product/ProductApi.java"
create_file "api/src/main/java/com/tradingplatform/api/core/product/ProductController.java"

# Create API core recommendation files
create_file "api/src/main/java/com/tradingplatform/api/core/recommendation/RecommendationApi.java"
create_file "api/src/main/java/com/tradingplatform/api/core/recommendation/RecommendationController.java"

# Create API core review files
create_file "api/src/main/java/com/tradingplatform/api/core/review/ReviewApi.java"
create_file "api/src/main/java/com/tradingplatform/api/core/review/ReviewController.java"

# Create API event files
create_file "api/src/main/java/com/tradingplatform/api/event/EventPublisher.java"
create_file "api/src/main/java/com/tradingplatform/api/event/EventSubscriber.java"
create_file "api/src/main/java/com/tradingplatform/api/event/models/OrderEvent.java"
create_file "api/src/main/java/com/tradingplatform/api/event/models/PortfolioEvent.java"
create_file "api/src/main/java/com/tradingplatform/api/event/models/PriceEvent.java"

# Create API exception files
create_file "api/src/main/java/com/tradingplatform/api/exceptions/GlobalExceptionHandler.java"
create_file "api/src/main/java/com/tradingplatform/api/exceptions/InvalidDataException.java"
create_file "api/src/main/java/com/tradingplatform/api/exceptions/NotFoundException.java"
create_file "api/src/main/java/com/tradingplatform/api/exceptions/ServiceException.java"

# Create auth0 directory structure and files
create_file "auth0/auth0-config.json"
create_file "auth0/login-callback.html"
create_file "auth0/logout-callback.html"
create_dir "auth0/rules"
create_file "auth0/rules/add-roles.js"
create_file "auth0/rules/enrich-tokens.js"
create_file "auth0/rules/whitelist-ips.js"

# Create common directory structure and files
create_file "common/build.gradle.kts"
create_dir "common/proto"
create_file "common/proto/market_data.proto"
create_file "common/proto/order.proto"
create_file "common/proto/portfolio.proto"
create_file "common/proto/product.proto"
create_dir "common/schemas"
create_file "common/schemas/market-data-schema.json"
create_file "common/schemas/order-schema.json"
create_file "common/schemas/portfolio-schema.json"
create_file "common/schemas/product-schema.json"
create_dir "common/src/main/java/com/tradingplatform/common"
create_dir "common/src/main/java/com/tradingplatform/common/config"
create_file "common/src/main/java/com/tradingplatform/common/config/CommonConfig.java"
create_file "common/src/main/java/com/tradingplatform/common/config/MetricsConfig.java"
create_dir "common/src/main/java/com/tradingplatform/common/dto"
create_file "common/src/main/java/com/tradingplatform/common/dto/ErrorResponse.java"
create_file "common/src/main/java/com/tradingplatform/common/dto/MarketDataDto.java"
create_file "common/src/main/java/com/tradingplatform/common/dto/OrderDto.java"
create_file "common/src/main/java/com/tradingplatform/common/dto/PortfolioDto.java"
create_file "common/src/main/java/com/tradingplatform/common/dto/ProductDto.java"
create_dir "common/src/main/java/com/tradingplatform/common/event"
create_file "common/src/main/java/com/tradingplatform/common/event/EventProcessor.java"
create_file "common/src/main/java/com/tradingplatform/common/event/EventPublisher.java"
create_dir "common/src/main/java/com/tradingplatform/common/utils"
create_file "common/src/main/java/com/tradingplatform/common/utils/DateUtils.java"
create_file "common/src/main/java/com/tradingplatform/common/utils/MathUtils.java"
create_file "common/src/main/java/com/tradingplatform/common/utils/ValidationUtils.java"
create_dir "common/src/main/java/com/tradingplatform/common/validation"
create_file "common/src/main/java/com/tradingplatform/common/validation/OrderValidator.java"
create_file "common/src/main/java/com/tradingplatform/common/validation/ValidationResult.java"

# Create Docker files
create_file "docker/docker-compose.prod.yml"
create_file "docker/docker-compose.yml"
create_file "docker/Dockerfile.api"
create_file "docker/Dockerfile.frontend"
create_file "docker/Dockerfile.template"
create_file "docker/Dockerfile.trading-engine"
create_dir "docker/nginx/conf.d"
create_file "docker/nginx/conf.d/default.conf"
create_file "docker/nginx/nginx.conf"
create_file "docker-compose.yml"

# Create docs directory structure
create_dir "docs/api/openapi-specs"
create_file "docs/api/openapi-specs/market-data-api.yaml"
create_file "docs/api/openapi-specs/order-api.yaml"
create_file "docs/api/openapi-specs/portfolio-api.yaml"
create_file "docs/api/openapi-specs/product-api.yaml"
create_dir "docs/api/postman"
create_file "docs/api/postman/trading-platform.postman_collection.json"
create_dir "docs/architecture"
create_file "docs/architecture/component-diagram.png"
create_file "docs/architecture/data-flow-diagram.png"
create_file "docs/architecture/service-interactions.png"
create_dir "docs/deployment"
create_file "docs/deployment/aws-architecture.png"
create_file "docs/deployment/kubernetes-deployment.yaml"
create_file "docs/deployment/scaling-strategy.md"
create_dir "docs/user-guides"
create_file "docs/user-guides/admin-guide.md"
create_file "docs/user-guides/developer-guide.md"
create_file "docs/user-guides/user-guide.md"

# Create engine directory structure
create_dir "engine/analytics-engine"
create_file "engine/analytics-engine/build.gradle.kts"
create_file "engine/analytics-engine/Dockerfile"
create_dir "engine/analytics-engine/src/main/java/com/tradingplatform/analytics"
create_file "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/AnalyticsApplication.java"
create_dir "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/config"
create_file "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/config/KafkaConfig.java"
create_file "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/config/SparkConfig.java"
create_dir "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/models"
create_file "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/models/AnalyticsResult.java"
create_file "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/models/MarketTrend.java"
create_dir "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/service" 
create_file "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/service/MarketAnalysisService.java"
create_file "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/service/PredictionService.java"
create_file "engine/analytics-engine/src/main/java/com/tradingplatform/analytics/service/TrendAnalysisService.java"
create_dir "engine/analytics-engine/src/main/resources"
create_file "engine/analytics-engine/src/main/resources/application.yml"
create_file "engine/analytics-engine/src/main/resources/logback.xml"

# Create trading engine directory structure
create_dir "engine/trading-engine/app"
create_dir "engine/trading-engine/app/api"
create_file "engine/trading-engine/app/api/order_api.py"
create_file "engine/trading-engine/app/api/position_api.py"
create_dir "engine/trading-engine/app/consumers"
create_file "engine/trading-engine/app/consumers/kafka_consumer.py"
create_file "engine/trading-engine/app/consumers/market_data_consumer.py"
create_dir "engine/trading-engine/app/core"
create_file "engine/trading-engine/app/core/engine.py"
create_file "engine/trading-engine/app/core/matching_engine.py"
create_file "engine/trading-engine/app/core/order_book.py"
create_file "engine/trading-engine/app/core/position_manager.py"
create_file "engine/trading-engine/app/main.py"
create_dir "engine/trading-engine/app/models"
create_file "engine/trading-engine/app/models/market_data.py"
create_file "engine/trading-engine/app/models/order.py"
create_file "engine/trading-engine/app/models/position.py"
create_file "engine/trading-engine/app/requirements.txt"
create_dir "engine/trading-engine/app/services"
create_file "engine/trading-engine/app/services/execution_service.py"
create_file "engine/trading-engine/app/services/notification_service.py"
create_file "engine/trading-engine/app/services/order_service.py"
create_file "engine/trading-engine/app/services/risk_manager.py"

# Create the Java service file
create_dir "engine/trading-engine/src/main/java/com/tradingplatform/engine/trading/service"
create_file "engine/trading-engine/src/main/java/com/tradingplatform/engine/trading/service/TradeService.java"

# Create frontend directory structure
create_file "frontend/angular.json"
create_file "frontend/Dockerfile"
create_file "frontend/package.json"
create_dir "frontend/src/app"
create_file "frontend/src/app/app-routing.module.ts"
create_file "frontend/src/app/app.component.html"
create_file "frontend/src/app/app.component.scss"
create_file "frontend/src/app/app.component.ts"
create_file "frontend/src/app/app.module.ts"

# Create frontend components
create_dir "frontend/src/app/components/auth/login"
create_file "frontend/src/app/components/auth/login/login.component.html"
create_file "frontend/src/app/components/auth/login/login.component.scss"
create_file "frontend/src/app/components/auth/login/login.component.ts"
create_dir "frontend/src/app/components/auth/logout"
create_file "frontend/src/app/components/auth/logout/logout.component.html"
create_file "frontend/src/app/components/auth/logout/logout.component.scss"
create_file "frontend/src/app/components/auth/logout/logout.component.ts"
create_dir "frontend/src/app/components/auth/register"
create_file "frontend/src/app/components/auth/register/register.component.html"
create_file "frontend/src/app/components/auth/register/register.component.scss"
create_file "frontend/src/app/components/auth/register/register.component.ts"
create_dir "frontend/src/app/components/dashboard"
create_file "frontend/src/app/components/dashboard/dashboard.component.html"
create_file "frontend/src/app/components/dashboard/dashboard.component.scss"
create_file "frontend/src/app/components/dashboard/dashboard.component.ts"
create_dir "frontend/src/app/components/dashboard/market-overview"
create_file "frontend/src/app/components/dashboard/market-overview/market-overview.component.html"
create_file "frontend/src/app/components/dashboard/market-overview/market-overview.component.scss"
create_file "frontend/src/app/components/dashboard/market-overview/market-overview.component.ts"
create_dir "frontend/src/app/components/dashboard/portfolio-summary"
create_file "frontend/src/app/components/dashboard/portfolio-summary/portfolio-summary.component.html"
create_file "frontend/src/app/components/dashboard/portfolio-summary/portfolio-summary.component.scss"
create_file "frontend/src/app/components/dashboard/portfolio-summary/portfolio-summary.component.ts"
create_dir "frontend/src/app/components/dashboard/watchlist"
create_file "frontend/src/app/components/dashboard/watchlist/watchlist.component.html"
create_file "frontend/src/app/components/dashboard/watchlist/watchlist.component.scss"
create_file "frontend/src/app/components/dashboard/watchlist/watchlist.component.ts"
create_dir "frontend/src/app/components/trading/chart"
create_file "frontend/src/app/components/trading/chart/chart.component.html"
create_file "frontend/src/app/components/trading/chart/chart.component.scss"
create_file "frontend/src/app/components/trading/chart/chart.component.ts"
create_dir "frontend/src/app/components/trading/order-book"
create_file "frontend/src/app/components/trading/order-book/order-book.component.html"
create_file "frontend/src/app/components/trading/order-book/order-book.component.scss"
create_file "frontend/src/app/components/trading/order-book/order-book.component.ts"
create_dir "frontend/src/app/components/trading/order-form"
create_file "frontend/src/app/components/trading/order-form/order-form.component.html"
create_file "frontend/src/app/components/trading/order-form/order-form.component.scss"
create_file "frontend/src/app/components/trading/order-form/order-form.component.ts"
create_dir "frontend/src/app/components/trading/trading-view"
create_file "frontend/src/app/components/trading/trading-view/trading-view.component.html"
create_file "frontend/src/app/components/trading/trading-view/trading-view.component.scss"
create_file "frontend/src/app/components/trading/trading-view/trading-view.component.ts"

# Create frontend guards
create_dir "frontend/src/app/guards"
create_file "frontend/src/app/guards/admin.guard.ts"
create_file "frontend/src/app/guards/auth.guard.ts"
create_file "frontend/src/app/guards/role.guard.ts"

# Create frontend models
create_dir "frontend/src/app/models"
create_file "frontend/src/app/models/market-data.model.ts"
create_file "frontend/src/app/models/order.model.ts"
create_file "frontend/src/app/models/portfolio.model.ts"
create_file "frontend/src/app/models/product.model.ts"
create_file "frontend/src/app/models/user.model.ts"

# Create frontend services
create_dir "frontend/src/app/services"
create_file "frontend/src/app/services/auth.service.ts"
create_file "frontend/src/app/services/market-data.service.ts"
create_file "frontend/src/app/services/notification.service.ts"
create_file "frontend/src/app/services/order.service.ts"
create_file "frontend/src/app/services/portfolio.service.ts"
create_file "frontend/src/app/services/product.service.ts"
create_file "frontend/src/app/services/websocket.service.ts"

# Create frontend shared components
create_dir "frontend/src/app/shared/components/footer"
create_file "frontend/src/app/shared/components/footer/footer.component.html"
create_file "frontend/src/app/shared/components/footer/footer.component.scss"
create_file "frontend/src/app/shared/components/footer/footer.component.ts"
create_dir "frontend/src/app/shared/components/header"
create_file "frontend/src/app/shared/components/header/header.component.html"
create_file "frontend/src/app/shared/components/header/header.component.scss"
create_file "frontend/src/app/shared/components/header/header.component.ts"
create_dir "frontend/src/app/shared/components/sidebar"
create_file "frontend/src/app/shared/components/sidebar/sidebar.component.html"
create_file "frontend/src/app/shared/components/sidebar/sidebar.component.scss"
create_file "frontend/src/app/shared/components/sidebar/sidebar.component.ts"

# Create frontend shared directives
create_dir "frontend/src/app/shared/directives"
create_file "frontend/src/app/shared/directives/click-outside.directive.ts"
create_file "frontend/src/app/shared/directives/number-only.directive.ts"

# Create frontend shared interceptors
create_dir "frontend/src/app/shared/interceptors"
create_file "frontend/src/app/shared/interceptors/auth.interceptor.ts"
create_file "frontend/src/app/shared/interceptors/error.interceptor.ts"

# Create frontend shared pipes
create_dir "frontend/src/app/shared/pipes"
create_file "frontend/src/app/shared/pipes/currency-formatter.pipe.ts"
create_file "frontend/src/app/shared/pipes/date-formatter.pipe.ts"

# Create frontend assets
create_dir "frontend/src/assets"
create_file "frontend/src/assets/favicon.ico"
create_dir "frontend/src/assets/fonts"
create_dir "frontend/src/assets/images"
create_dir "frontend/src/assets/json"

# Create frontend environments
create_dir "frontend/src/environments"
create_file "frontend/src/environments/environment.prod.ts"
create_file "frontend/src/environments/environment.ts"

# Create frontend core files
create_index_html "frontend/src/index.html"
create_file "frontend/src/main.ts"
create_file "frontend/src/styles.scss"
create_file "frontend/tsconfig.app.json"
create_file "frontend/tsconfig.json"
create_file "frontend/tsconfig.spec.json"

# Create gradle files
create_dir "gradle/wrapper"
create_file "gradle/wrapper/gradle-wrapper.jar"
create_file "gradle/wrapper/gradle-wrapper.properties"
create_file "gradle.properties"

# Create common files
create_file "build.gradle.kts"
create_file "Changelog.md"
create_file "clean-up.sh"
create_file "LICENSE"
create_file "README.md"
create_file "settings.gradle.kts"
create_file "test-em-all.bash"

# Create microservices directory structure and files
create_microservice_structure() {
    local service_name="$1"
    local base_dir="microservices/$service_name"
    
    create_dir "$base_dir/src/main/java/com/tradingplatform/$service_name"
    create_dir "$base_dir/src/main/resources"
    create_dir "$base_dir/src/test/java/com/tradingplatform/$service_name"
    create_dir "$base_dir/src/test/resources"
    
    # Create main application file
    local class_name=$(echo "$service_name" | sed -r 's/(^|-)([a-z])/\U\2/g')
    create_file "$base_dir/src/main/java/com/tradingplatform/$service_name/${class_name}Application.java"
    
    # Create standard directories
    create_dir "$base_dir/src/main/java/com/tradingplatform/$service_name/config"
    create_dir "$base_dir/src/main/java/com/tradingplatform/$service_name/controller"
    create_dir "$base_dir/src/main/java/com/tradingplatform/$service_name/service"
    create_dir "$base_dir/src/main/java/com/tradingplatform/$service_name/repository"
    create_dir "$base_dir/src/main/java/com/tradingplatform/$service_name/model"
    
    # Create build file
    if [[ "$service_name" == "product-composite-service" || "$service_name" == "product-service" ]]; then
        create_file "$base_dir/build.gradle"
    else
        create_file "$base_dir/build.gradle.kts"
    fi
}  # Added missing function closure

# ... [rest of the original script content] ...

# ======== ADD THESE LINES AFTER THE MAIN SCRIPT CONTENT ========
# Create additional microservices
create_microservice_structure "recommendation-service"
create_microservice_structure "review-service"
create_microservice_structure "userauth-service" 
create_microservice_structure "reporting-service"
create_microservice_structure "analytics-service"

# Create realtime-data-service specific files
create_microservice_structure "realtime-data-service"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/dto/PriceUpdate.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/dto/MarketDataMessage.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/repository/MarketDataRepository.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/repository/QuoteRepository.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/config/WebSocketSecurityConfig.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/config/KafkaConfig.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/config/WebSocketConfig.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/entity/Quote.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/entity/MarketData.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/controller/WebSocketController.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/controller/MarketDataController.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/service/impl/MarketDataServiceImpl.java"
create_file "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata/service/impl/QuoteServiceImpl.java"

# Create websocket-service (Node.js) structure
create_dir "microservices/websocket-service/src/middlewares"
create_dir "microservices/websocket-service/src/routes"
create_dir "microservices/websocket-service/src/services"
create_file "microservices/websocket-service/src/app.ts"
create_file "microservices/websocket-service/src/config/index.ts"
create_file "microservices/websocket-service/src/utils/auth.ts"

# Add missing .DS_Store files from file-list.txt
touch "microservices/.DS_Store"
touch "microservices/portfolio-service/.DS_Store"
touch "microservices/realtime-data-service/.DS_Store"
touch "util/.DS_Store"
touch "util/src/.DS_Store"
touch "engine/.DS_Store"
touch "spring-cloud/.DS_Store"

echo "Folder structure generation completed!"
    
   

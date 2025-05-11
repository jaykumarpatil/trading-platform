#!/bin/zsh

# Set the workspace root
WORKSPACE_ROOT="/Users/jaykumarpatil/Projects/trading-platform"
cd "$WORKSPACE_ROOT"

# Create required directories if they don't exist
declare -a dirs=(
  "api/src/main/java/com/tradingplatform/api"
  "common/src/main/java/com/tradingplatform/common"
  "engine/analytics-engine/src/main/java/com/tradingplatform/analytics"
  "engine/trading-engine/src/main/java/com/tradingplatform/engine"
  "microservices/admin-service/src/main/java/com/tradingplatform/admin"
  "microservices/order-service/src/main/java/com/tradingplatform/order"
  "microservices/portfolio-service/src/main/java/com/tradingplatform/portfolio"
  "microservices/product-service/src/main/java/com/tradingplatform/product"
  "microservices/realtime-data-service/src/main/java/com/tradingplatform/realtimedata"
  "spring-cloud/authorization-server/src/main/java/com/tradingplatform/auth"
  "spring-cloud/eureka-server/src/main/java/com/tradingplatform/eureka"
  "spring-cloud/gateway/src/main/java/com/tradingplatform/gateway"
)

for dir in "${dirs[@]}"; do
  mkdir -p "$dir"
  mkdir -p "${dir/main/test}"
done

# Remove duplicate build files
for service in microservices/*-service; do
  if [ -f "$service/build.gradle" ] && [ -f "$service/build.gradle.kts" ]; then
    rm "$service/build.gradle"
  fi
done

# Generate tree structure
if ! command -v tree &> /dev/null; then
  echo "Installing tree command..."
  brew install tree
fi

tree -I 'node_modules|dist|target|build|.git|.gradle|.idea|.vscode' \
     --charset UTF-8 \
     --dirsfirst \
     -F \
     > folder-structure.txt

echo "✅ Fixed folder structure and generated tree output"

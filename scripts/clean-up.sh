#!/bin/bash

echo "🧹 Starting project cleanup..."

# Angular cleanup
echo "Cleaning Angular files..."
find . -type d -name "node_modules" -exec rm -rf {} +
find . -type d -name "dist" -exec rm -rf {} +
find . -type f -name "package-lock.json" -exec rm {} +
find . -type f -name ".angular/cache" -exec rm -rf {} +

# Node.js cleanup
echo "Cleaning Node.js files..."
find . -type d -name ".npm" -exec rm -rf {} +
find . -type f -name ".npmrc" -exec rm {} +
find . -type d -name ".cache" -exec rm -rf {} +

# Gradle cleanup
echo "Cleaning Gradle files..."
find . -type d -name "build" -exec rm -rf {} +
find . -type d -name ".gradle" -exec rm -rf {} +
find . -type d -name "out" -exec rm -rf {} +
find . -type f -name "*.class" -exec rm {} +

# Python cleanup
echo "Cleaning Python files..."
find . -type d -name "__pycache__" -exec rm -rf {} +
find . -type d -name "*.egg-info" -exec rm -rf {} +
find . -type d -name ".pytest_cache" -exec rm -rf {} +
find . -type d -name ".coverage" -exec rm -rf {} +
find . -type f -name "*.pyc" -exec rm {} +
find . -type f -name "*.pyo" -exec rm {} +
find . -type f -name ".coverage" -exec rm {} +

# VSCode cleanup
echo "Cleaning VSCode files..."
find . -type d -name ".vscode-test" -exec rm -rf {} +
find . -type f -name ".DS_Store" -exec rm {} +

# Temp files cleanup
echo "Cleaning temporary files..."
find . -type f -name "*~" -exec rm {} +
find . -type f -name "*.swp" -exec rm {} +
find . -type f -name "*.swo" -exec rm {} +
find . -type f -name "*.log" -exec rm {} +

echo "✨ Cleanup complete!"
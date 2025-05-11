## Operator Interaction
- When asked to fix code, first explain the problems found.
- When asked to generate tests, first explain what tests will be created.
- When making multiple changes, provide a step-by-step overview first.

## Security
- Check the code for vulnerabilities after generating.
- Avoid hardcoding sensitive information like credentials or API keys.
- Use secure coding practices and validate all inputs.

## Environment Variables
- If a .env file exists, use it for local environment variables
- Document any new environment variables in README.md
- Provide example values in .env.example

## Version Control
- Keep commits atomic and focused on single changes
- Follow conventional commit message format
- Update .gitignore for new build artifacts or dependencies

## Code Style
- Follow existing project code style and conventions
- Add type hints and docstrings for all new functions
- Include comments for complex logic

## Change Logging
- Each time you generate code, note the changes in changelog.md
- Follow semantic versioning guidelines
- Include date and description of changes

## Testing Requirements
- Include unit tests for new functionality
- Maintain minimum 80% code coverage
- Add integration tests for API endpoints

## For Python Projects Only
- Always use Python3.9 or higher
- Always use a Python3 virtual environment: if no venv exists, create one and activate
- Always use and update a requirements.txt file for Python modules
- Follow PEP 8 style guidelines
- Include type hints (PEP 484)

## For Angular/TypeScript Projects Only
- Always use Angular 19 or higher
- Always use TypeScript 5.7 or higher
- Use `npm` or `yarn` for package managemen
- Include type definitions for all new modules
- Follow TypeScript coding guidelines
- Use ESLint for linting and formatting
- Use Jasmine/Karma for unit testing
- Use Canvas and WebGl for rendering
- Use `ng serve` for running the application
- Ingnore using external libraries like jQuery or Bootstrap

## For Java Projects Only
- Use `Maven` or `Gradle` for dependency management
- Use `JUnit` for unit testing
- Use `Checkstyle` for code formatting
- Use `PMD` for static code analysis

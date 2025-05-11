# Trading Platform

This project is a comprehensive, trading platform. It is designed for high performance, reliability, and scalability, enabling users to execute trades, monitor market data, and manage their portfolios effectively.

## Features

* **Real-time Market Data:** Integration with financial data providers for live price feeds, order book updates, and historical data.
* **Order Management System (OMS):** Sophisticated order types (limit, market, stop-loss, etc.), order routing, and execution management.
* **Portfolio Management:** Track holdings, performance analytics, profit/loss calculations, and risk exposure.
* **User Authentication & Authorization:** Secure login, multi-factor authentication, and role-based access control.
* **Trading Analytics:** Tools for technical analysis, charting, and strategy backtesting.
* **Reporting:** Generate statements, trade confirmations, and tax reports.
* **Admin Dashboard:** For system monitoring, user management, and operational control.
* **API Access:** For algorithmic trading and third-party integrations.

## Technologies Used

* **Frontend**: Angular 19 (core framework only, no external UI component libraries)
* **Backend**:
    * Node.js 21 (for real-time communication e.g., WebSockets, API gateways, and specific microservices)
    * Python 3 (for core trading logic, data analysis, machine learning components, and backend REST/gRPC APIs)
    * Java 21 
* **Database**: PostgreSQL (Primary transactional data), InfluxDB (Time-series market data)
* **Messaging Queue**: Apache Kafka (for event-driven architecture, order processing, market data dissemination)
* **Caching**: Redis (for session management, frequently accessed data, market data snapshots)
* **Containerization & Orchestration**: Docker, Kubernetes
* **CI/CD**: GitLab CI/CD (or Jenkins, GitHub Actions)
* **API Documentation**: Swagger/OpenAPI

## Prerequisites

Before you begin, ensure you have the following installed:

* Node.js 21.x and npm (or yarn)
* Python 3.x (preferably 3.9+) and pip
* Angular CLI (latest compatible version for Angular 19: `npm install -g @angular/cli@19`)
* Docker and Docker Compose (latest stable versions)
* Kubernetes CLI (`kubectl`) (if deploying to Kubernetes)
* PostgreSQL client tools
* Access to Kafka and Redis instances (local or remote)

## Project Structure

```
trading-platform/
|
|-- frontend-angular/                     # Angular 19 frontend application
|   |-- src/
|   |   |-- app/                          # Core application module
|   |   |   |-- components/               # UI components (e.g., charts, order-form, portfolio-display)
|   |   |   |   |-- dashboard/
|   |   |   |   |-- trading/
|   |   |   |   |-- auth/
|   |   |   |-- services/                 # Angular services (e.g., auth.service, market-data.service, order.service)
|   |   |   |-- models/                   # Frontend data models/interfaces
|   |   |   |-- guards/                   # Route guards (e.g., auth.guard)
|   |   |   |-- shared/                   # Shared modules, components, pipes, directives
|   |   |   |-- app-routing.module.ts
|   |   |   |-- app.component.html
|   |   |   |-- app.component.scss
|   |   |   |-- app.component.ts
|   |   |   |-- app.module.ts
|   |   |-- assets/                       # Static assets (images, fonts, etc.)
|   |   |-- environments/                 # Environment-specific configurations
|   |   |   |-- environment.ts
|   |   |   |-- environment.prod.ts
|   |   |-- index.html
|   |   |-- main.ts
|   |   |-- polyfills.ts
|   |   |-- styles.scss                   # Global styles
|   |   |-- test.ts
|   |-- .editorconfig
|   |-- .gitignore
|   |-- angular.json                    # Angular CLI configuration
|   |-- karma.conf.js                   # Karma test runner config
|   |-- package.json
|   |-- package-lock.json
|   |-- tsconfig.app.json
|   |-- tsconfig.json
|   |-- tsconfig.spec.json
|   |-- tslint.json                     # (or eslint.json if migrated)
|
|-- backend-nodejs/                     # Node.js services (e.g., API Gateway, WebSocket Server)
|   |-- gateway-api/                    # Example: API Gateway service
|   |   |-- src/
|   |   |   |-- controllers/
|   |   |   |-- routes/
|   |   |   |-- services/
|   |   |   |-- middlewares/
|   |   |   |-- config/
|   |   |   |-- utils/
|   |   |   |-- app.js
|   |   |   |-- server.js
|   |   |-- tests/
|   |   |-- package.json
|   |   |-- .env.example
|   |   |-- Dockerfile
|   |-- websocket-service/              # Example: Real-time WebSocket service
|   |   |-- src/
|   |   |-- tests/
|   |   |-- package.json
|   |   |-- .env.example
|   |   |-- Dockerfile
|
|-- backend-python/                     # Python services (e.g., Trading Engine, Analytics, User Service)
|   |-- trading-engine/                 # Core trading logic
|   |   |-- app/
|   |   |   |-- api/                      # REST/gRPC API definitions and handlers
|   |   |   |-- core/                     # Business logic (order matching, risk checks)
|   |   |   |-- models/                   # Database models (e.g., SQLAlchemy or Django ORM)
|   |   |   |-- services/                 # External service integrations
|   |   |   |-- consumers/                # Kafka consumers
|   |   |   |-- config.py
|   |   |   |-- main.py                   # Application entry point (e.g., FastAPI, Flask)
|   |   |-- tests/
|   |   |-- requirements.txt
|   |   |-- .env.example
|   |   |-- Dockerfile
|   |-- analytics-service/              # Data analysis and reporting
|   |   |-- app/
|   |   |-- tests/
|   |   |-- requirements.txt
|   |   |-- .env.example
|   |   |-- Dockerfile
|
|-- common/                             # Shared libraries, data contracts, utilities (if any, for cross-service use)
|   |-- proto/                          # Protobuf definitions for gRPC if used
|   |-- schemas/                        # JSON schemas for Kafka messages
|
|-- docs/                               # Project documentation
|   |-- api/                            # API documentation (e.g., OpenAPI/Swagger JSON/YAML files)
|   |-- architecture.md                 # System architecture overview
|   |-- deployment.md                   # Deployment guides
|
|-- scripts/                            # Build, deployment, and utility scripts
|   |-- setup_dev_env.sh
|   |-- run_linters.sh
|   |-- run_tests.sh
|   |-- deploy_staging.sh
|
|-- docker-compose.yml                  # For local development environment setup
|-- .env.example                        # Example environment variables for the whole project
|-- .gitignore                          # Global gitignore
|-- README.md                           # This file
|-- LICENSE                             # Project License
|-- CHANGELOG.md                        # Change history
```

## Setup and Installation

Detailed instructions for setting up each component of the platform:

1.  **Clone the repository**:
    ```bash
    git clone <your-repository-url>
    cd production-trading-platform
    ```

2.  **Configure Global Environment Variables**:
    Copy `production-trading-platform/.env.example` to `production-trading-platform/.env` and update it with global configurations if any. Specific service-level environment files (`<service>/.env.example` to `<service>/.env`) should also be configured.

3.  **Setup Shared Services (Database, Kafka, Redis)**:
    * If using Docker for local development, you can often start these with:
        ```bash
        docker-compose up -d postgresql influxdb kafka redis # (Or specific services)
        ```
    * Ensure schemas/topics are initialized if necessary. Refer to `docs/deployment.md` or specific service readmes.

4.  **Setup Frontend (Angular)**:
    ```bash
    cd frontend-angular
    cp .env.example .env # If you have frontend specific env vars
    npm install
    cd ..
    ```

5.  **Setup Backend Node.js Services**:
    For each Node.js service (e.g., `gateway-api`, `websocket-service`):
    ```bash
    cd backend-nodejs/<service-name>
    cp .env.example .env
    npm install
    cd ../..
    ```

6.  **Setup Backend Python Services**:
    For each Python service (e.g., `trading-engine`, `analytics-service`):
    ```bash
    cd backend-python/<service-name>
    python3 -m venv venv
    source venv/bin/activate  # On Windows use `venv\Scripts\activate`
    pip install -r requirements.txt
    cp .env.example .env
    # Potentially database migrations if using an ORM
    # Example: alembic upgrade head (if using Alembic with SQLAlchemy)
    cd ../..
    ```

## Running the Application for Local Development

1.  **Start Dependent Services**:
    Ensure your PostgreSQL, Kafka, Redis, and InfluxDB instances are running (e.g., via `docker-compose up -d`).

2.  **Start Backend Services**:
    * **Python Services**:
        Open a terminal for each Python service, navigate to its directory (e.g., `backend-python/trading-engine`), activate the virtual environment (`source venv/bin/activate`), and run the application (e.g., `python main.py` or `uvicorn main:app --reload` for FastAPI with auto-reload).
    * **Node.js Services**:
        Open a terminal for each Node.js service, navigate to its directory (e.g., `backend-nodejs/gateway-api`), and run the application (e.g., `npm run dev` or `npm start`).

3.  **Start Frontend Application**:
    * Navigate to `frontend-angular`:
        ```bash
        cd frontend-angular
        npm start # This usually runs 'ng serve'
        ```

4.  **Access the Platform**:
    Open your browser and navigate to `http://localhost:4200` (or the port configured for Angular in `angular.json`). The API Gateway (Node.js) might be running on a different port (e.g., `http://localhost:3000/api`).

## Development Workflow

Developing a production-grade trading platform involves coordinated efforts across frontend, backend, and infrastructure.

**1. Branching Strategy:**
   * Follow a Gitflow-like workflow:
        * `main` (or `master`): Production-ready code. Only merge from `develop` for releases.
        * `develop`: Integration branch for features. This is where feature branches are merged.
        * `feature/<feature-name>`: For new features. Branch off `develop`.
        * `bugfix/<bug-name>`: For fixing bugs in `develop` or `main`.
        * `hotfix/<hotfix-name>`: For critical production bugs. Branch off `main` and merge back to `main` and `develop`.
        * `release/<version>`: For preparing a new production release. Branch off `develop`.

**2. Environment Management:**
   * Use `.env` files for local environment configuration. **Never commit actual `.env` files.** Commit `.env.example` files as templates.
   * For different environments (dev, staging, prod), use environment-specific configuration management (e.g., Kubernetes ConfigMaps/Secrets, Vault, or cloud provider's configuration services).

**3. Frontend Development (Angular):**
   * **Component Development:** Create components in `frontend-angular/src/app/components/`. Follow Angular style guides.
   * **Service Implementation:** Implement data fetching and business logic in services (`frontend-angular/src/app/services/`).
   * **State Management:** Consider a state management library (e.g., NgRx or Akita) for complex applications if needed, though the requirement was "without any external framework dependencies" which might imply avoiding these unless "framework" specifically meant UI frameworks. If so, rely on Angular services and RxJS.
   * **Routing:** Define routes in `app-routing.module.ts`. Use route guards for authentication/authorization.
   * **Styling:** Use SCSS with a clear structure (e.g., global styles in `styles.scss`, component-specific styles).
   * **Testing:**
        * Unit Tests: Write unit tests for components and services using Jasmine and Karma (`ng test`). Aim for high code coverage.
        * End-to-End (E2E) Tests: Use Protractor (or alternative like Cypress if preferred, though Protractor is default with older Angular CLI) for E2E testing (`ng e2e`).
   * **Linting/Formatting:** Use ESLint (or TSLint if older project) and Prettier. Configure them in `package.json` and run via `npm run lint` and `npm run format`.
        ```bash
        npm run lint
        npm run format
        ```
   * **Local Server:** `ng serve` provides a development server with live reloading.

**4. Backend Development (Node.js & Python):**
   * **API Design & Contract:**
        * Design APIs first (e.g., using OpenAPI/Swagger). Store the specification in `docs/api/`.
        * For gRPC, define services and messages in `.proto` files in `common/proto/`.
        * Ensure frontend and backend teams agree on the API contracts.
   * **Service Development (Java 21 - e.g., ):**
        * Structure your application in Hexagonal Arachitecure 
        * Implement business logic in service layers.
        * For Kafka, implement consumers and producers carefully, ensuring message schemas are consistent.
   * **Service Development (Node.js - e.g., Express.js):**
        * Similar structure: routes/controllers, services, middlewares.
        * If using TypeScript (recommended), leverage its strong typing.
        * For WebSocket services, manage connections, rooms, and message broadcasting efficiently.
   * **Testing:**
        * Unit Tests: Use `pytest` for Python, and Jest or Mocha/Chai for Node.js.
        * Integration Tests: Test interactions between different components within a service (e.g., API endpoint to database).
        * Contract Tests: (e.g., using Pact) to ensure services can communicate correctly.
   * **Linting/Formatting:**
        * Python: Black, Flake8, isort. Configure in `pyproject.toml` or `setup.cfg`.
        * Node.js: ESLint, Prettier.
   * **Debugging:** Use IDE debuggers or built-in debuggers (`pdb` for Python, Node.js inspector).
   * **Hot Reloading:** Tools like `uvicorn --reload` (FastAPI), `nodemon` (Node.js) can auto-restart services on code changes.

**5. Inter-Service Communication:**
   * Clearly define how services communicate (REST, gRPC, message queues).
   * Use service discovery mechanisms in production (e.g., Kubernetes services, Consul).
   * During local development, services might communicate via `localhost` and different ports defined in their `.env` files.

**6. Database Migrations (Python/ORM):**
   * If using an ORM with migration tools (e.g., Alembic for SQLAlchemy, Django Migrations), run migrations as part of development and deployment.
     ```bash
     # Example for Alembic
     alembic revision -m "create_orders_table"
     alembic upgrade head
     ```

**7. Code Reviews:**
   * All code changes should go through pull/merge requests and be reviewed by at least one other developer.
   * Focus on correctness, performance, security, readability, and adherence to coding standards.

**8. CI/CD Pipeline:**
   * Automate linting, testing, building Docker images, and deployments through your CI/CD pipeline (e.g., defined in `.gitlab-ci.yml`).

## API Endpoints (High-Level Overview)

Refer to the OpenAPI/Swagger documentation located in `/docs/api/`. Key gateways and service groups:

* **Authentication Service (`/auth`):**
    * `POST /auth/login`
    * `POST /auth/register`
    * `POST /auth/refresh-token`
* **Market Data Service (`/market`):**
    * `GET /market/data/{symbol}`
    * `GET /market/historical/{symbol}?period=1d`
    * `GET /market/orderbook/{symbol}` (via WebSocket or REST)
* **Order Management Service (`/orders`):**
    * `POST /orders` - Place a new order
    * `GET /orders` - List user's orders
    * `GET /orders/{orderId}` - Get order status
    * `DELETE /orders/{orderId}` - Cancel an order
* **Portfolio Service (`/portfolio`):**
    * `GET /portfolio` - Get user's current holdings and performance
    * `GET /portfolio/history` - Get portfolio value history

## Deployment

Refer to `docs/deployment.md` for detailed instructions on deploying to staging and production environments using Docker and Kubernetes. The CI/CD pipeline automates most of this process.

## Contributing

Contributions are welcome! Please fork the repository, create a feature branch based on `develop`, and submit a pull request. Ensure your code adheres to the project's coding standards and includes relevant tests and documentation updates.

1.  Fork the Project.
2.  Create your Feature Branch: `git checkout -b feature/YourAmazingFeature develop`
3.  Commit your Changes: `git commit -m 'Add some AmazingFeature'`
4.  Push to the Branch: `git push origin feature/YourAmazingFeature`
5.  Open a Pull Request against the `develop` branch.

## License

Distributed under the MIT License. See `LICENSE` file for more information.

## Contact

Trading Platform Team - jaypatil3004@gmail.com

Project Link: [https://github.com/jaykumarpatil/trading-platform](https://github.com/jaykumarpatil/trading-platform)
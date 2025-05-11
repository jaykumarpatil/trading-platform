# Trading Platform

This project is a comprehensive trading platform designed for high performance, reliability, and scalability. It enables users to execute trades, monitor market data, and manage their portfolios effectively through a modern, microservices-based architecture.

## Features

* **Real-time Market Data:** Integration with financial data providers for live price feeds, order book updates, and historical data
* **Order Management System (OMS):** Sophisticated order types (limit, market, stop-loss, etc.), order routing, and execution management
* **Portfolio Management:** Track holdings, performance analytics, profit/loss calculations, and risk exposure
* **User Authentication & Authorization:** Secure login, multi-factor authentication, and role-based access control
* **Trading Analytics:** Tools for technical analysis, charting, and strategy backtesting
* **Reporting:** Generate statements, trade confirmations, and tax reports
* **Admin Dashboard:** System monitoring, user management, and operational control
* **API Access:** RESTful and WebSocket APIs for algorithmic trading and third-party integrations

## Architecture Overview

The platform is built using a modern microservices architecture with three main components:

### Frontend (Angular)
- Single Page Application built with Angular 19
- Real-time data updates via WebSocket
- Responsive design and modern UI/UX
- Located in `/frontend-angular`

### Backend Services
1. **Java Microservices** (`/backend-java`)
   - Order Management Service
   - Portfolio Service
   - User Authentication Service
   - Admin Service
   - Reporting Service
   - Trading Analytics Service
   - Real-time Data Service

2. **Node.js Services** (`/backend-nodejs`)
   - API Gateway
   - WebSocket Service for real-time data

3. **Infrastructure Services**
   - PostgreSQL for transactional data
   - InfluxDB for time-series market data
   - Redis for caching and session management
   - Kafka for event streaming

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

## Project Setup

### Prerequisites

* Node.js 21.x and npm
* Java 21 JDK
* Docker and Docker Compose
* Kubernetes (optional)
* PostgreSQL 15+
* Redis 7+
* Apache Kafka 3+
* InfluxDB 2+

### Initial Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/jaykumarpatil/trading-platform.git
   cd trading-platform
   ```

2. **Frontend Setup**
   ```bash
   cd frontend-angular
   npm install
   npm run start
   ```
   Frontend will be available at http://localhost:4200

3. **Java Backend Setup**
   ```bash
   cd backend-java
   ./gradlew build
   ```
   This will build all Java microservices

4. **Node.js Services Setup**
   ```bash
   # Setup API Gateway
   cd backend-nodejs/gateway-api
   npm install
   npm run dev

   # Setup WebSocket Service
   cd ../websocket-service
   npm install
   npm run dev
   ```

5. **Infrastructure Setup**
   ```bash
   cd docker
   docker-compose up -d
   ```
   This will start PostgreSQL, Redis, Kafka, and InfluxDB

### Development Workflow

1. **Frontend Development**
   - Run `npm run start` in `frontend-angular` for development server
   - Run `npm run test` for unit tests
   - Run `npm run e2e` for end-to-end tests
   - Run `npm run lint` for linting

2. **Java Services Development**
   - Use `./gradlew bootRun` in respective service directories
   - Use `./gradlew test` for running tests
   - Use `./gradlew build` for building services

3. **Node.js Services Development**
   - Use `npm run dev` for development with hot reload
   - Use `npm test` for running tests
   - Use `npm run build` for production builds

### API Documentation

- REST API documentation is available at `http://localhost:3000/api-docs`
- WebSocket API documentation is in `/docs/websocket-api.md`
- Each microservice's API documentation is available at their respective Swagger UI endpoints

### Deployment

1. **Docker Deployment**
   ```bash
   docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
   ```

2. **Kubernetes Deployment**
   ```bash
   kubectl apply -f docker/k8s/
   ```

Detailed deployment instructions are available in `/docs/deployment.md`

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

Jay Kumar Patil - jaypatil3004@gmail.com

Project Link: [https://github.com/jaykumarpatil/trading-platform](https://github.com/jaykumarpatil/trading-platform)
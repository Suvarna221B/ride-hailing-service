# 🚗 Ride Hailing Platform - Backend

A production-ready ride-hailing platform backend built with Spring Boot, featuring real-time ride matching, driver allocation, payment processing, and comprehensive monitoring with New Relic APM.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Monitoring](#monitoring)
- [Testing](#testing)
- [Contributing](#contributing)

## ✨ Features

### Core Functionality
- **User Management**: Registration and authentication with JWT tokens
- **Ride Lifecycle**: Request → Match → Accept → Start → Complete → Payment
- **Real-time Driver Matching**: Redis Geo-based proximity search
- **Payment Processing**: Asynchronous payment handling via Kafka
- **Role-based Access Control**: Separate permissions for riders and drivers

### Technical Features
- **Event-Driven Architecture**: Kafka for asynchronous messaging
- **Caching & Geospatial**: Redis for driver location tracking
- **Strategy Pattern**: Extensible ride update handling
- **Comprehensive Logging**: Structured logging with SLF4J
- **APM Monitoring**: New Relic integration for performance tracking
- **RESTful API**: Clean, well-documented endpoints

## 🛠 Tech Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.x |
| **Language** | Java 21 |
| **Database** | PostgreSQL |
| **Cache/Geo** | Redis |
| **Messaging** | Apache Kafka |
| **Security** | Spring Security + JWT |
| **Monitoring** | New Relic APM |
| **Build Tool** | Maven |
| **Testing** | JUnit 5, Mockito |

## documentation : https://docs.google.com/document/d/1RGyaAMKJMJNH0q9LQow5XzKJX02NpUE58cPJI1TnCws/edit?usp=sharing


## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose
- New Relic account (optional, for monitoring)

### 1. Clone the Repository

```bash
git clone https://github.com/Suvarna221B/ride-hailing-service.git
cd ride-hailing-service
```

### 2. Start Infrastructure Services

```bash
docker-compose up -d
```

This starts:
- PostgreSQL (port 5432)
- Redis (port 6379)
- Kafka + Zookeeper (port 9093)

### 3. Configure Application

Update `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ridehailing
spring.datasource.username=postgres
spring.datasource.password=postgres

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Kafka
spring.kafka.bootstrap-servers=localhost:9093

# JWT Secret (change in production!)
jwt.secret=your-secret-key-here
```

### 4. Set New Relic License Key (Optional)

```bash
export NEW_RELIC_LICENSE_KEY='your-license-key-here'
```

### 5. Build and Run

```bash
# Build
mvn clean install

# Run with New Relic
java -javaagent:target/agents/newrelic-agent.jar \
     -jar target/ride-hailing-platform-0.0.1-SNAPSHOT.jar

# Or run without New Relic
mvn spring-boot:run
```

The application will start on **http://localhost:8081**

### 6. Create Test Users

```bash
# Create a rider
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "rider1",
    "password": "password123",
    "role": "RIDER"
  }'

# Create a driver
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "driver1",
    "password": "password123",
    "role": "DRIVER"
  }'
```


## ⚙️ Configuration

### Application Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Application port | 8081 |
| `jwt.secret` | JWT signing key | (required) |
| `jwt.expiration` | Token expiration (ms) | 86400000 (24h) |
| `redis.ttl.seconds` | Driver location TTL | 600 (10 min) |
| `spring.kafka.consumer.group-id` | Kafka consumer group | ride-hailing-group |

### Environment Variables

- `NEW_RELIC_LICENSE_KEY`: New Relic license key for APM monitoring
- `SPRING_PROFILES_ACTIVE`: Active profile (dev, test, prod)

## 📊 Monitoring

### New Relic APM

The application is instrumented with New Relic for:
- **Transaction Tracing**: API endpoint performance
- **Database Monitoring**: Query performance and bottlenecks
- **Error Tracking**: Exception monitoring
- **Custom Metrics**: Business KPIs

Configuration: `src/main/resources/newrelic.yml`

### Logging

Structured logging with SLF4J/Logback:
- Request/response logging
- Business event tracking
- Error logging with stack traces

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=RideServiceTest
```

### Test Coverage

```bash
mvn jacoco:report
```

Report available at: `target/site/jacoco/index.html`

### Key Test Classes

- `RideServiceTest`: Ride lifecycle and business logic
- `DriverServiceTest`: Driver location and status management
- `PaymentServiceTest`: Payment processing
- `RideControllerTest`: API endpoint integration tests

## 🔒 Security

- **JWT Authentication**: Stateless token-based auth
- **Role-based Access Control**: `@RequiredRole` annotations
- **Password Encryption**: BCrypt hashing
- **CORS Configuration**: Configurable allowed origins
- **Input Validation**: Bean Validation (JSR-380)

## 📁 Project Structure

```
src/main/java/com/example/ridehailing/
├── annotation/          # Custom annotations (@RequiredRole)
├── config/             # Configuration classes
│   ├── KafkaConfig.java
│   ├── RedisConfig.java
│   └── SecurityConfig.java
├── controller/         # REST controllers
│   ├── AuthController.java
│   ├── RideController.java
│   ├── DriverController.java
│   └── FareController.java
├── dto/               # Data Transfer Objects
├── exception/         # Custom exceptions
├── kafka/            # Kafka publishers/consumers
│   ├── publisher/
│   └── consumer/
├── model/            # JPA entities
├── repository/       # JPA repositories
├── security/         # Security components
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
├── service/          # Business logic
│   ├── RideService.java
│   ├── DriverService.java
│   ├── PaymentService.java
│   └── strategy/     # Strategy pattern implementations
└── util/             # Utility classes
```



---

**Frontend Repository**: [ride-hailing-frontend](https://github.com/Suvarna221B/ride-hailing-frontend) (React + Vite)

For questions or support, please open an issue on GitHub.

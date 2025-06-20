# 🍽️ Restaurant Food Ordering API – Backend

This is the backend RESTful API for the **Restaurant Food Ordering App**, built with **Spring Boot**, providing services such as user authentication, product management, order handling, and integration with ZaloPay for payments.

---

## 📦 Technologies

- Java 17+
- Spring Boot
- Spring Security + JWT
- MySQL
- ZaloPay SDK (payment integration)
- Maven
- RESTful API

---

## 🚀 Getting Started

### 🔧 Prerequisites

- JDK 17+
- Maven
- MySQL 8+
- Postman (for testing API)

### ⚙️ Configuration

Create a `.env` or update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/restaurant_db
    username: root
    password: your_password

jwt:
  secret: your_jwt_secret
  expiration: 3600000
```

---

### ▶️ Run Locally

```bash
# Build the project
mvn clean install

# Run the server
mvn spring-boot:run
```

---


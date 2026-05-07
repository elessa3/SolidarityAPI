# 🌍 SolidarityAPI - Immigrant Support System

[![Java](https://img.shields.io/badge/Java-17-blue)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-orange)](https://jwt.io/)
[![Render](https://img.shields.io/badge/Render-Live-success)](https://solidarityapi.onrender.com)
[![Tests](https://img.shields.io/badge/Tests-31%20passing-brightgreen)](https://github.com/elessa3/SolidarityAPI)

## 📌 About the Project

RESTful API for managing care for immigrants in vulnerable situations, developed to support non-governmental organizations (such as an NGO that works with immigrants in Europe). The system allows you to register immigrants, record service attendances, manage volunteers, and maintain case histories.

**Motivation:** Facilitate the work of institutions that help immigrants regularize their status, access basic services, and receive social support.

## 🛠️ Technologies Used

- **Java 17** with Functional Programming (Streams, Optional)
- **Spring Boot 3.2** (Web, Data JPA, Security, Validation)
- **PostgreSQL** as relational database
- **JWT** for stateless authentication
- **Maven** for dependency management
- **Lombok** to reduce boilerplate
- **JUnit 5 & Mockito** for testing
- **SpringDoc OpenAPI** for documentation
- **Render** for cloud deployment

## 🌐 Live API

The API is live at: [https://solidarityapi.onrender.com/swagger-ui.html](https://solidarityapi.onrender.com/swagger-ui.html)

## 📦 How to Run Locally

```bash
# Clone the repository
git clone https://github.com/elessa3/SolidarityAPI.git

# Set up PostgreSQL (using Docker)
docker run --name solidarity-db -e POSTGRES_DB=solidaritydb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=123456 -p 5432:5432 -d postgres:16

# Configure environment variables
export DB_URL=jdbc:postgresql://localhost:5432/solidaritydb
export DB_USER=admin
export DB_PASSWORD=123456
export JWT_SECRET=yourSecretKey

# Run the application
./mvnw spring-boot:run


📖 API Documentation
After running locally, access: http://localhost:8080/swagger-ui.html

Sample Request (Create Immigrant)
json
POST /api/immigrants
Authorization: Bearer {token}
{
  "fullName": "Maria Silva",
  "nationality": "Venezuela",
  "birthDate": "1990-05-15",
  "status": "PENDING"
}

🧪 Testing
bash
./mvnw test
# Test coverage: ~85% (service and controller layers)

🤝 Contributing
Suggestions are welcome! Open an issue or submit a pull request.

📄 License
MIT © Érica Lessa

📬 Contact
LinkedIn: Érica Lessa

GitHub: @elessa3
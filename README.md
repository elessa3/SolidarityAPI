# 🌍 Immigrant Support API

[![Java](https://img.shields.io/badge/Java-17-blue)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Authentication-orange)](https://jwt.io/)
[![Render](https://img.shields.io/badge/Render-Deploy-success)](https://render.com)

## 📌 About the Project

API RESTful for the management of care to immigrants in vulnerable situation, developed to support non-governmental organizations (such as the NGO that works with immigrants in Europe). The system allows to register immigrants, register care, manage volunteers and maintain case history.

**Motivation:** Facilitate the work of institutions that help immigrants regularize, access basic services and receive social support.

## 🚀 Key Features

- Authentication and authorization with JWT (roles: ADMIN, VOLUNTEER)
- Complete CRUD for immigrants (only ADMIN)
- Registration of appointments with description and date
- Listing of immigrants with filters by status and nationality
- History of care per immigrant
- Paging on all list endpoints
- Overall exception treatment
- Interactive documentation with Swagger UI

## 🛠️ Technologies Used

- **Java 17** with Functional Programming (Streams, Optional)
- **Spring Boot 3.2** (Web, Data JPA, Security, Validation)
- **PostgreSQL** as a relational database
- **JWT** for stateless authentication
- **Maven** for dependency management
- **Lombok** to reduce boilerplate
- **JUnit 5 & Mockito** for testing
- **SpringDoc OpenAPI** for documentation
- **Render** to deploy cloud

## 📦 How to Run Locally

`s bash
# Clone the repository
git clone https://github.com/elessa3/immigrant-support-api.git

# Set up PostgreSQL (or use Docker)
docker run --name immigrant-db -e POSTGRES_DB=immigrantdb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=123456 -p 5432:5432 -d postgres:16

# Configure the environment variables in the application.properties file or use:
export DB_URL=jdbc:postgresql://localhost:5432/immigrantdb
export DB_USER=admin
export DB_PASSWORD=123456
export JWT_SECRET=minhaChaveSecretaSuperSegura123

# Run the application
./mvnw spring-boot:run
`

## 📖 API documentation

After running, go to: `http://localhost:8080/swagger-ui.html`

### Sample Request (Create Immigrant)

`s json
POST /immigrants
Authorization: Bearer {token}
{
  "fullName": "Maria Silva",
  "nationality": "Venezuela",
  "birthDate": "1990-05-15",
  "status": "PENDING"
}
`

## 🧪 Testing

`s bash
./mvnw test
# Test coverage: 85% (service and controller classes)
`

## 🌐 Deploy

API available at: [https://immigrant-support-api.onrender.com](https://immigrant-support-api.onrender.com)

## 🤝 Contribution

Suggestions are welcome! Open an issue or send a pull request.

## 📄 License

MIT © Érica Lessa

## 📬 Contact

- LinkedIn: [Érica Lessa](https://www.linkedin.com/in/erica-lessa)
- GitHub: [@elessa3](https://github.com/elessa3)

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar apenas pom.xml primeiro (cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código e fazer build
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
# Estágio 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar apenas o pom.xml primeiro (para aproveitar cache)
COPY pom.xml .

# Baixar dependências (camada de cache)
RUN mvn dependency:go-offline

# Copiar o código fonte
COPY src ./src

# Compilar e gerar o JAR
RUN mvn clean package -DskipTests

# Estágio 2: Execução (imagem menor)
FROM eclipse-temurin:17-jre-alpine

# Definir diretório de trabalho
WORKDIR /app

# Copiar o JAR gerado do estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expor a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
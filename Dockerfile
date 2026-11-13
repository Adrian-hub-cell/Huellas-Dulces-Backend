
# Etapa 1: Construcción
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src src

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Instalar curl para el healthcheck
RUN apk add --no-cache curl

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

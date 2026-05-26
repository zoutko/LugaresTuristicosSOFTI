# ---- Etapa 1: Compilar ----
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven primero (optimiza caché)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Etapa 2: Ejecutar ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar solo el .jar generado
COPY --from=build /app/target/gestor-recorridos-turisticos-0.0.1-SNAPSHOT.jar app.jar

# Render inyecta el puerto via variable de entorno PORT
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
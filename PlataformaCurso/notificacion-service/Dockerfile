# ============================================================
# Etapa 1: Build - compila el jar dentro del contenedor
# (reproducible: no depende de un target/ construido a mano)
# ============================================================
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# Se copia primero el pom.xml para aprovechar la cache de capas
# de Docker: si no cambian las dependencias, no se vuelven a
# descargar en cada build.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ============================================================
# Etapa 2: Runtime - imagen liviana, solo JRE, sin herramientas
# de build ni código fuente
# ============================================================
FROM eclipse-temurin:17-jre-alpine

# Usuario no-root por seguridad (buena práctica de Docker)
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

COPY --from=build /build/target/notificacion-service-0.0.1-SNAPSHOT.jar app.jar
RUN chown spring:spring app.jar
USER spring

EXPOSE 8086

HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD wget --spider -q http://localhost:8086/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

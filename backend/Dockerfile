FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

COPY --from=builder /app/build/libs/*.jar app.jar

RUN mkdir -p /data/geneinator && chown -R appuser:appgroup /data

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

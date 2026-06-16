FROM eclipse-temurin:17-jre-alpine

LABEL description="Spring Cloud Config Server"

ARG BUILD_ID=local
ENV BUILD_ID=${BUILD_ID}

WORKDIR /app

COPY target/*.jar spring-config-server.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8686

ENTRYPOINT ["java", "-jar", "spring-config-server.jar"]

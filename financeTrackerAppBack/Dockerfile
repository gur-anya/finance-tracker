FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY . .

RUN mvn dependency:go-offline

RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/financeTrackerAppBack/target/financeTrackerAppBack-1.0-SNAPSHOT.jar /app/financeTracker.jar

ENTRYPOINT ["java", "-jar", "/app/financeTracker.jar"]
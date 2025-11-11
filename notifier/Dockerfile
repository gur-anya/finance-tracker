FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .

COPY financeTrackerAppBack/pom.xml ./financeTrackerAppBack/
COPY notifier/pom.xml ./notifier/
COPY commonDTOs/pom.xml ./commonDTOs/
COPY auditionStarter/pom.xml ./auditionStarter/
COPY loggingStarter/pom.xml ./loggingStarter/

RUN mvn dependency:go-offline

COPY . .

RUN mvn clean install -DskipTests


FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/notifier/target/notifier-0.0.1-SNAPSHOT.jar ./notifier.jar

ENTRYPOINT ["java", "-jar", "./notifier.jar"]
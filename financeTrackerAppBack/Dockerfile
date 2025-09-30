FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .

COPY commonDTOs/pom.xml ./commonDTOs/
COPY auditionStarter/pom.xml ./auditionStarter/
COPY loggingStarter/pom.xml ./loggingStarter/
COPY financeTrackerAppBack/pom.xml ./financeTrackerAppBack/

RUN mvn dependency:go-offline

COPY commonDTOs/src ./commonDTOs/src/
COPY auditionStarter/src ./auditionStarter/src/
COPY loggingStarter/src ./loggingStarter/src/
COPY financeTrackerAppBack/src ./financeTrackerAppBack/src/

RUN mvn clean install -DskipTests


FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/financeTrackerAppBack/target/*.jar ./financeTrackerAppBack.jar

ENTRYPOINT ["java", "-jar", "./financeTrackerAppBack.jar"]
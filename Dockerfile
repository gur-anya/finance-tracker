FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY mvnw pom.xml ./
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/financeTracker-1.0-SNAPSHOT-jar-with-dependencies.jar /app/financeTracker-1.0-SNAPSHOT-jar-with-dependencies.jar
RUN mkdir -p src/main/resources
COPY src/main/resources/db-changelog src/main/resources
RUN cd src/main/resources && \
    touch config.properties && \
    echo "username=newAdmin" >> config.properties && \
    echo "password=5678" >> config.properties && \
    echo "url=jdbc:postgresql://tracker_app_database:5432/tracker_app_database" >> config.properties && \
    echo "changeLogFile=/app/src/main/resources/db-changelog/main-changelog.xml" >> config.properties
ENTRYPOINT ["java", "-jar", "/app/financeTracker-1.0-SNAPSHOT-jar-with-dependencies.jar"]
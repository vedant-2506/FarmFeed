<<<<<<< HEAD
FROM eclipse-temurin:21-jdk

=======
# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-21 AS build
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

<<<<<<< HEAD
# Copy current build context (farmFeed directory)
COPY . .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the jar
RUN ./mvnw clean package -DskipTests

=======
# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/farmfeed-1.0.0.jar app.jar
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# Java 17 base image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build Spring Boot app
RUN ./mvnw clean package -DskipTests

# App runs on port 8080
EXPOSE 8080

# Start the app
CMD ["java", "-jar", "target/*.jar"]

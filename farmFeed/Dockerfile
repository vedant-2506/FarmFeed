FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy current build context (farmFeed directory)
COPY . .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the jar
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/farmfeed-1.0.0.jar"]

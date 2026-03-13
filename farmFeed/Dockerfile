FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the farmFeed subfolder contents
COPY farmFeed/ .

# Make mvnw executable
RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/farmfeed-1.0.0.jar"]

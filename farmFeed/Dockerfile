FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the farmFeed subfolder (where pom.xml and mvnw are)
COPY farmFeed/ .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the jar
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/farmfeed-1.0.0.jar"]

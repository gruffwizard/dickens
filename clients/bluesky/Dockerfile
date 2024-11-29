FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY  target/bluesky-1.0-SNAPSHOT.jar app.jar
COPY target/libs/* .
# Command to run the application
CMD ["java", "-jar", "app.jar"]

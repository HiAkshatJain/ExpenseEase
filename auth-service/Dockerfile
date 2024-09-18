# Use the official OpenJDK 22 base image
FROM openjdk

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the host to the container
COPY app.jar /app/app.jar

# Expose the port that your Java service listens on
EXPOSE 9010

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
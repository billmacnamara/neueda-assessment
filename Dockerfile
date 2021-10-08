FROM java:8-alpine
VOLUME /tmp
ADD target/neueda-assessment.jar app.jar
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]

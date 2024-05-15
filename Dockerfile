FROM madiva/openjdk17:latest
COPY target/backend05-1.0.jar /usr/local/backend.jar
CMD ["java","-jar","/usr/local/backend.jar"]

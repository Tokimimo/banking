FROM openjdk:jre-alpine

COPY target/banking-thorntail.jar /opt/banking-thorntail.jar

EXPOSE 8080
# preferIPv4Stack is needed to keep thorntail happy
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "/opt/banking-thorntail.jar"]
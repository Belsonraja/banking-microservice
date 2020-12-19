FROM openjdk:8
EXPOSE 8082
ADD ./target/transaction-service-1.0.0.jar transaction-service-1.0.0.jar
ENTRYPOINT ["java", "-jar", "/transaction-service-1.0.0.jar"]
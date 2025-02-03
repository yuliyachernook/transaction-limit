FROM openjdk:17-jdk-alpine

WORKDIR /build

COPY target/transaction-limit-1.0-SNAPSHOT.jar /build/transaction-limit.jar

ENTRYPOINT ["java", "-jar", "transaction-limit.jar"]
FROM openjdk:17-jdk-alpine

WORKDIR /build

COPY target/transaction-transactionLimit-1.0-SNAPSHOT.jar /build/transaction-transactionLimit.jar

ENTRYPOINT ["java", "-jar", "transaction-transactionLimit.jar"]
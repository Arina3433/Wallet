FROM openjdk:17-jdk
WORKDIR /app
COPY target/Wallet-0.0.1.jar Wallet-0.0.1.jar
ENTRYPOINT ["java", "-jar", "Wallet-0.0.1.jar", "--spring.profiles.active=prod"]
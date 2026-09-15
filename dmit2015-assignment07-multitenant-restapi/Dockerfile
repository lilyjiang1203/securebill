FROM docker.io/library/eclipse-temurin:21-jre

WORKDIR /app

COPY target/securebill-multitenant-api-bootable.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "-b=0.0.0.0"]
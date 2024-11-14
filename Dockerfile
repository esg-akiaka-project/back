FROM openjdk:17-jdk

WORKDIR /app

RUN rm -rf /app/*.jar

COPY build/libs/harudoyak-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]


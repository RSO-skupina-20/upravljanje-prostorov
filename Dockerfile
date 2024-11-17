FROM adoptopenjdk:16-jre-openj9-focal

RUN mkdir /app

WORKDIR /app

ADD ./api/target/api-1.0-SNAPSHOT.jar /app

EXPOSE 8080

ENV DB_URL=jdbc:postgresql://host.docker.internal:5433/prostori
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres

ENTRYPOINT ["java", "-jar", "api-1.0-SNAPSHOT.jar"]

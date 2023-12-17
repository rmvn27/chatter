FROM gradle:jdk21-jammy AS build
WORKDIR /app

COPY . /app
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:21-jammy

RUN apt-get update && \
  apt-get install -y tini

COPY --from=build /app/build/libs/*-all.jar /app.jar

ENV CHATTER_CONFIG_LOCATION="/data/config.json"
EXPOSE 8080
ENTRYPOINT ["/usr/bin/tini", "--"]
CMD ["java", "-jar", "/app.jar"]

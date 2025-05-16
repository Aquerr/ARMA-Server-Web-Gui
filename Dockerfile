FROM maven:3.9.9-eclipse-temurin-21 as builder

COPY . .

RUN mvn install -U

FROM steamcmd/steamcmd:ubuntu-24

USER aswg

WORKDIR /aswg

COPY --from=builder target/arma-server-web-gui-*.jar /aswg/aswg.jar

EXPOSE 8085/tcp

ENV LANG="en_US.UTF-8"
ENV LC_ALL="en_US.UTF-8"

CMD ["java", "-jar", "/aswg/aswg.jar"]
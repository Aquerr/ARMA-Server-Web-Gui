FROM eclipse-temurin:21-jre as runner

USER root

LABEL maintainer=Aquerr

# Intall SteamCMD
RUN apt-get -y update \
    && apt-get -y install lib32gcc-s1 \
    && mkdir /steamcmd \
    && cd /steamcmd \
    && curl -sqL "https://steamcdn-a.akamaihd.net/client/installer/steamcmd_linux.tar.gz" | tar zxvf -

# Intall ASWG
RUN groupadd --gid 1001 aswg \
    && useradd --home-dir /home/aswg --create-home --uid 1001 --gid 1001 aswg \
    && mkdir /aswg \
    && mkdir /aswg/config \
    && mkdir /aswg/data \
    && mkdir /aswg/arma-server/ \
    && mkdir /aswg/arma-server/mods \
    && chown aswg:aswg -R /aswg \
    && chown aswg:aswg -R /steamcmd

WORKDIR /aswg

COPY ./target/arma-server-web-gui-*.jar /aswg/aswg.jar

# ASWG Properties
ENV ASWG_STEAMCMD_PATH="/steamcmd/steamcmd.sh"

ENV ASWG_MODSDIRECTORYPATH="./mods"
ENV ASWG_SERVERDIRECTORYPATH="./arma-server"
ENV ASWG_CONFIG_DIR="./config"

ENV LANG="en_US.UTF-8"
ENV LANGUAGE="en_US:en"
ENV LC_ALL="en_US.UTF-8"

HEALTHCHECK --interval=2m --timeout=5s --retries=3 \
  CMD curl -f http://localhost:8085/api/v1/actuator/health || exit 1

VOLUME ["/aswg/arma-server", "/aswg/data", "/aswg/config"]

EXPOSE 8085/tcp
EXPOSE 2302-2306/udp

USER aswg:aswg

ENTRYPOINT ["java", "-jar", "/aswg/aswg.jar"]
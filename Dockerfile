FROM eclipse-temurin:25-jre AS runner

ARG VERSION=0.0.0

USER root

LABEL maintainer=Aquerr
LABEL description="user-friendly management GUI for Arma 3 server"
LABEL version="${VERSION}"

ENV APP_USER=aswg
ENV APP_GROUP=aswg

# Intall SteamCMD
RUN apt-get -y update \
    && apt-get -y install lib32gcc-s1 \
    && apt-get -y install curl \
    && mkdir /steamcmd \
    && cd /steamcmd \
    && curl -sqL "https://steamcdn-a.akamaihd.net/client/installer/steamcmd_linux.tar.gz" | tar zxvf -

# Install GOSU
RUN apt-get update \
 && apt-get install --update -y gosu \
 && rm -rf /var/lib/apt/lists/* \
    gosu nobody true

# Intall ASWG
RUN mkdir /aswg \
    && mkdir /aswg/config \
    && mkdir /aswg/data \
    && mkdir /aswg/logs \
    && mkdir /aswg/arma-server/ \
    && mkdir /aswg/arma-server/mods

WORKDIR /aswg

COPY ./target/arma-server-web-gui-*.jar /aswg/aswg.jar
COPY --chmod=755 entrypoint.sh /entrypoint.sh

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

ENTRYPOINT ["/entrypoint.sh"]
CMD ["java", "-jar", "/aswg/aswg.jar"]
FROM debian:stable-20220328 as run

# install curl to download calibre and wildfly
# install unzip to install wildfly
RUN apt-get update && \
    apt-get install -y \
        curl=7.74.0-1.3+deb11u1 \
        unzip=6.0-26 \
    && rm -rf /var/lib/apt/lists/*

# download and install wildfly
ENV VERSION 26.1.0.Final
ENV INSTALL_DIR /opt
ENV WILDFLY_HOME ${INSTALL_DIR}/wildfly-preview-${VERSION}
WORKDIR ${WILDFLY_HOME}
ENV DEPLOYMENT_DIR ${WILDFLY_HOME}/standalone/deployments/
ENV CONFIGURATION_DIR ${WILDFLY_HOME}/standalone/configuration
RUN useradd -b /opt -s /bin/sh -d ${INSTALL_DIR} serveradmin && echo serveradmin:serveradmin | chpasswd
RUN curl -L -O https://github.com/wildfly/wildfly/releases/download/${VERSION}/wildfly-preview-${VERSION}.zip \
    && unzip wildfly-preview-${VERSION}.zip -d ${INSTALL_DIR} \
    && rm wildfly-preview-${VERSION}.zip \
    && chown -R serveradmin:serveradmin /opt \
    && chmod a+x ${WILDFLY_HOME}/bin/standalone.sh \
    && chmod -R a+rw ${INSTALL_DIR}
USER serveradmin
ENV JAVA_OPTS=--enable-preview
ENTRYPOINT ${WILDFLY_HOME}/bin/standalone.sh -b=0.0.0.0 -bmanagement=0.0.0.0
EXPOSE 8080
EXPOSE 9990

# download and install calibre
USER root
# install python to install calibre
# install xzutils to install calibre
RUN apt-get update && \
    apt-get install -y \
        xz-utils=5.2.5-2 \
        python3=3.9.2-3 \
    && rm -rf /var/lib/apt/lists/*

RUN curl -L -O https://download.calibre-ebook.com/linux-installer.sh \
    && sh linux-installer.sh version=5.40.0 \
    && rm linux-installer.sh

# install openjdk-17-jre-headless to run slushy
# install libgl1 to run calibre
RUN apt-get update && \
    apt-get install -y \
        openjdk-17-jre-headless=17.0.2+8-1~deb11u1 \
        libgl1=1.3.2-1 \
    && rm -rf /var/lib/apt/lists/*

FROM maven:3.8.4-openjdk-17-slim as build

WORKDIR /build

# download dependencies
ADD pom.xml .
RUN mvn package -Dmaven.test.skip -Declipselink.weave.skip

# compile
ADD src src
RUN mvn package

# run
FROM run

USER serveradmin

# deploy from build into wildfly
WORKDIR ${DEPLOYMENT_DIR}
COPY --from=build /build/target/slushy.war .
RUN ${WILDFLY_HOME}/bin/add-user.sh admin admin

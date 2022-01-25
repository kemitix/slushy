FROM maven:3.8.4-openjdk-17-slim as build

WORKDIR /build

COPY pom.xml .
RUN mvn package -Dmaven.test.skip -Declipselink.weave.skip

COPY src src
RUN mvn package

FROM debian:stable-20211220 as run

# install wget and python required to download and install calibre, plus a JRE
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        wget=1.21-1+deb11u1 \
        curl=7.74.0-1.3+deb11u1 \
        unzip=6.0-26 \
        python3=3.9.2-3 \
        openjdk-17-jre-headless=17~19-1 \
        xz-utils=5.2.5-2 \
    && rm -rf /var/lib/apt/lists/*

# download and install calibre
RUN curl -L -O https://download.calibre-ebook.com/linux-installer.sh \
    && sh linux-installer.sh version=5.35.0 \
    && rm linux-installer.sh

# download and install wildfly
ENV VERSION 26.0.1.Final
ENV INSTALL_DIR /opt
ENV WILDFLY_HOME ${INSTALL_DIR}/wildfly-preview-${VERSION}
WORKDIR ${WILDFLY_HOME}
ENV DEPLOYMENT_DIR ${WILDFLY_HOME}/standalone/deployments/
ENV CONFIGURATION_DIR ${WILDFLY_HOME}/standalone/configuration
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN useradd -b /opt -s /bin/sh -d ${INSTALL_DIR} serveradmin && echo serveradmin:serveradmin | chpasswd \
    && curl -L -O https://github.com/wildfly/wildfly/releases/download/${VERSION}/wildfly-preview-${VERSION}.zip \
    && unzip wildfly-preview-${VERSION}.zip -d ${INSTALL_DIR} \
    && rm wildfly-preview-${VERSION}.zip \
    && chown -R serveradmin:serveradmin /opt \
    && chmod a+x ${WILDFLY_HOME}/bin/standalone.sh \
    && chmod -R a+rw ${INSTALL_DIR}
USER serveradmin
ENV JAVA_OPTS=--enable-preview
ENTRYPOINT ["./bin/standalone.sh", "-b=0.0.0.0", "-bmanagement=0.0.0.0"]
EXPOSE 8080
EXPOSE 9990

# deploy from build into wildfly
WORKDIR ${DEPLOYMENT_DIR}
COPY --from=build /build/target/slushy.war .
RUN ${WILDFLY_HOME}/bin/add-user.sh admin admin

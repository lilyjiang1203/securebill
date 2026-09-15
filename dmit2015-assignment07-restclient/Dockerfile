FROM docker.io/library/eclipse-temurin:21-jre

WORKDIR /opt/wildfly

COPY target/server/ /opt/wildfly/

EXPOSE 8080

CMD ["/opt/wildfly/bin/standalone.sh", "-b=0.0.0.0"]
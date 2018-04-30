FROM openjdk:8-jdk as builder

WORKDIR /microservice

ARG MVN_VERSION=3.5.2

RUN wget -q http://mirrors.ae-online.de/apache/maven/maven-3/$MVN_VERSION/binaries/apache-maven-$MVN_VERSION-bin.zip -P /tmp \
    && unzip /tmp/apache-maven-$MVN_VERSION-bin.zip -d /tmp \
    && ln -s /tmp/apache-maven-$MVN_VERSION/bin/mvn /usr/local/bin/

COPY src ./src
COPY pom.xml .

RUN mvn clean install -Dmaven.test.skip=true

FROM openjdk:8-jre

WORKDIR /microservice/

RUN curl -s -H 'X-JFrog-Art-Api:AKCp2VpEdD1yvM7ATezQkjUnwvVB9yDp6jFy2D3moAGLuhrq7eY7BMUa634exLeX1kHExi6rv' "https://consortit.jfrog.io/consortit/generic-artifacts-local/vaultenv/vaultenv" -o /usr/local/bin/vaultenv

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["/bin/sh", "-c"]
CMD ["java -Xmx128m -server -jar /microservice/jira-adapter.jar -Duser.timezone=UTC"]

COPY --from=builder /microservice/target/jira-adapter-*with-depen*.jar /microservice/jira-adapter.jar
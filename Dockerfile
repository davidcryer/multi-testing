ARG GO_DEPENDENCY_LABEL_BASE_JAVA_11

FROM eu.gcr.io/at-artefacts/platform-base-java-11:$GO_DEPENDENCY_LABEL_BASE_JAVA_11 as build

LABEL maintainer="tom.edge@autotrader.co.uk"

COPY --chown=atcloud:atcloud . .

RUN ./mvnw clean --quiet --settings maven-settings.xml
RUN ./mvnw install --quiet --settings maven-settings.xml

FROM eu.gcr.io/at-artefacts/platform-base-java-11:$GO_DEPENDENCY_LABEL_BASE_JAVA_11
COPY --from=build /usr/local/autotrader/app/target/paf-importer.jar /usr/local/autotrader/app/app.jar
EXPOSE 8088

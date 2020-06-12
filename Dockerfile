# build stage
FROM maven:3.6.3-openjdk-11 as target
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /build/src/
RUN mvn package


FROM openjdk:11

ARG AWS_SKEY=""
ARG AWS_AKEY=""
ENV AWS_SKEY="${AWS_SKEY}"
ENV AWS_AKEY="${AWS_AKEY}"

EXPOSE 4567
COPY --from=target /build/target/*.jar /app/my-app.jar
CMD exec java $JAVA_OPTS -jar /app/my-app.jar

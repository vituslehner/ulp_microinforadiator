FROM resin/raspberrypi3-openjdk:openjdk-8u131-jdk
MAINTAINER Vitus Lehner <student@vitus-lehner.de>

RUN dpkg --purge --force-depends ca-certificates-java && \
    apt-get update && \
    apt-get install -f \
    apt-get install ca-certificates-java && \
    apt-get install python python-dev sense-hat

ADD . /opt/ulp-mir-source
WORKDIR /opt/ulp-mir-source

CMD bash ./gradlew clean build bootRun
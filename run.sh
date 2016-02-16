#!/bin/bash
mkdir -p target/website
mkdir -p target/transactions
JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8005,server=y,suspend=n"

#dylan-0.0.1-SNAPSHOT
#          -jar target/*-jar-with-dependencies.jar
#          -jar target/dylan-0.0.1-SNAPSHOT.jar

rm -rf target/dylan

mvn package && \
java $JAVA_OPTS \
          -Ddylan.store=src/test/resources/ \
          -DPORT=8085 \
          -Drestolino.packageprefix=com.github.davidcarboni.dylan.api \
          -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG \
           -jar target/*-jar-with-dependencies.jar

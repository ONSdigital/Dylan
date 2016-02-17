#!/bin/bash
mkdir -p target/website
mkdir -p target/transactions
JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8005,server=y,suspend=n"

#dylan-0.0.1-SNAPSHOT
#          -jar target/*-jar-with-dependencies.jar
#          -jar target/dylan-0.0.1-SNAPSHOT.jar

#rm -rf target/dylan

#mvn clean package -Dmaven.test.skip=true && \
#java $JAVA_OPTS \
#          -Ddylan.store=src/test/resources/ \
#          -DPORT=8085 \
#          -Drestolino.packageprefix=com.github.davidcarboni.dylan.api \
#          -Dorg.slf4j.simpleLogger.defaultLogLevel=INFO \
#          -jar target/*-jar-with-dependencies.jar


export RESTOLINO_STATIC="src/main/resources/files"
export RESTOLINO_CLASSES="target/classes"
export PACKAGE_PREFIX=com.github.davidcarboni.dylan.api
export PORT="8085"

mvn clean package dependency:copy-dependencies -Dmaven.test.skip=true && \
java $JAVA_OPTS \
 -Drestolino.files=$RESTOLINO_STATIC \
 -Drestolino.classes=$RESTOLINO_CLASSES \
 -Drestolino.packageprefix=$PACKAGE_PREFIX \
 -DSTART_EMBEDDED_SERVER=Y \
 -cp "target/classes:target/dependency/*" \
 com.github.davidcarboni.restolino.Main

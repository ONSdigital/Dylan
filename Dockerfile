FROM onsdigital/java-component

# Add the build artifacts
WORKDIR /usr/src
ADD git_commit_id /usr/src/
ADD ./target/dependency /usr/src/target/dependency
ADD ./target/classes /usr/src/target/classes
#ADD ./target/*-jar-with-dependencies.jar /usr/src/target/


# SSH port
EXPOSE 2222

# Set the entry point
ENTRYPOINT java -Xmx2048m \
   -Drestolino.files=target/web \
   -Drestolino.classes=target/classes \
   -Drestolino.packageprefix=com.github.davidcarboni.dylan.api \
   -cp "target/classes:target/dependency/*" \
   com.github.davidcarboni.restolino.Main
#!/bin/bash -eux

pushd dylan
  mvn clean package dependency:copy-dependencies -DskipTests=true
popd

cp -r dylan/target/* target/

#!/bin/bash -eux

pushd dylan
  mvn -DskipTests=true clean package dependency:copy-dependencies
  cp -r Dockerfile.concourse target/* ../build/
popd

#!/bin/bash -eux

pushd dylan
  mvn clean surefire:test
popd

#!/bin/bash

if [[ $(docker inspect --format="{{ .State.Running }}" dylan) == "false" ]]; then
  exit 1;
fi

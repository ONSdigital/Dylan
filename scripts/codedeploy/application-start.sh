#!/bin/bash

ECR_REPOSITORY_URI=
GIT_COMMIT=

docker run -d --name dylan                                       \
  --publish=2320:2320                                            \
  --volume=/var/lib/dylan/authorized_keys:/dylan/authorized_keys \
  --volume=/var/lib/dylan/csdb:/dylan/csdb                       \
  --volume=/var/lib/dylan/scp:/dylan/scp                         \
  --env=CSDB_DATA_DIR=/dylan/csdb                                \
  --env=SCP_AUTHORIZED_KEYS=/dylan/authorized_keys               \
  --env=SCP_ROOT_DIR=/dylan/scp                                  \
  --env=SSH_PORT=2320                                            \
  --env=recipient.url=http://zebedee:8080                        \
  --net=publishing                                               \
  --restart=always                                               \
  $ECR_REPOSITORY_URI/dylan:$GIT_COMMIT

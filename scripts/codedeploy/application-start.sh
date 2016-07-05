#!/bin/bash

ECR_REPOSITORY_URI=
GIT_COMMIT=

docker run -d                                                    \
  --env=CSDB_DATA_DIR=/dylan/csdb                                \
  --env=SCP_AUTHORIZED_KEYS=/dylan/authorized_keys               \
  --env=SCP_ROOT_DIR=/dylan/scp                                  \
  --env=SSH_PORT=2320                                            \
  --env=recipient.url=http://zebedee:8080                        \
  --name=dylan                                                   \
  --net=publishing                                               \
  --publish=2320:2320                                            \
  --restart=always                                               \
  --volume=/var/lib/dylan/authorized_keys:/dylan/authorized_keys \
  --volume=/var/lib/dylan/csdb:/dylan/csdb                       \
  --volume=/var/lib/dylan/scp:/dylan/scp                         \
  $ECR_REPOSITORY_URI/dylan:$GIT_COMMIT

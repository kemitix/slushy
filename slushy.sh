#!/usr/bin/env bash

docker run -d --rm \
       -e TRELLO_KEY \
       -e TRELLO_SECRET \
       -e SLUSHY_USER \
       -e SLUSHY_BOARD \
       -e SLUSHY_SENDER \
       -e SLUSHY_READER \
       -e AWS_ACCESS_KEY \
       -e AWS_SECRET_KEY \
       -e AWS_REGION \
       kemitix/slushy

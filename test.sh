#!/usr/bin/env bash

set -e

cd $(dirname $0)

docker build -f Dockerfile -t kemitix/slushy .

docker stop slushy && docker rm slushy
source slushy.sh
. logs.sh

#!/usr/bin/env bash

set -e

cd $(dirname $0)

git checkout master
git reset --hard
git pull origin master

mvn install
docker build -f slushy/src/main/docker/Dockerfile -t kemitix/slushy slushy

docker stop slushy
docker rm slushy
source slushy.sh

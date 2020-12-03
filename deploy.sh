#!/usr/bin/env bash

set -e

cd $(dirname $0)

git checkout master
git reset --hard
git pull origin master

if [ $# -gt 0 ];then
    echo "Deploying $1"
    git co $1
else
    echo "Deploying latest master"
fi

mvn install
docker build -f slushy/src/main/docker/Dockerfile -t kemitix/slushy slushy

docker stop slushy
docker rm slushy
source slushy.sh

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

docker build -f Dockerfile -t kemitix/slushy .

docker stop slushy && docker rm slushy
source slushy.sh

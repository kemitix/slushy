#!/usr/bin/env bash

set -e

BRANCH=$(git branch | grep '^*' | cut -b 3-)
if [ ! "$BRANCH" == "master" ]; then
	echo "ERROR: not on master branch: $BRANCH"
	exit 1
fi

if [ ! -z "$(git status --short)" ]; then
	echo "ERROR: worktree is not clean"
	exit 1
fi

docker stop slushy && docker rm slushy
purge-slushy-queue-live
source slushy.sh
time sh logs.sh

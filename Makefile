TAG := kemitix/slushy:$(shell git describe --tags)

default: docker

check:
	./trunk check

docker:
	docker build . -t ${TAG}	

stop:
	docker stop slushy || true

rm: stop
	docker rm slushy || true

run: rm
	docker run -d \
       --name slushy \
       --restart always \
       --expose 9990 \
       -e TRELLO_KEY \
       -e TRELLO_SECRET \
       -e SLUSHY_USER \
       -e SLUSHY_BOARD \
       -e SLUSHY_SENDER \
       -e SLUSHY_READER \
       -e SLUSHY_WEBHOOK \
       -e SLUSHY_QUEUE \
       -e SLUSHY_CONFIG_LIST \
       -e SLUSHY_CONFIG_CARD \
       -e SLUSHY_STATUS_LIST \
       -e SLUSHY_STATUS_CARD \
       -e AWS_ACCESS_KEY_ID \
       -e AWS_SECRET_ACCESS_KEY \
       -e AWS_REGION \
       ${TAG}

logs:
	docker logs -f slushy

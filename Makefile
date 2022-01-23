TAG := kemitix/slushy:$(shell git describe --tags)

docker:
	docker build . -t ${TAG}	
	docker tag ${TAG} kemitix/slushy

run: docker
	docker run -it \
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

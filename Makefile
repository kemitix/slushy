default: docker

install: .install

.install:
	mvn install
	touch .install

clean:
	mvn clean
	if [ -f .install ];then rm .install;fi

dev:
	mvn quarkus:dev \
		-Dslushy.inbox.scan-period=30000 \
		-Dslushy.reader.scan-period=30000 \
		-Dslushy.reject.scan-period=30000 \
		-Dslushy.reject.required-age-hours=0 \
		-Dslushy.hold.scan-period=30000 \
		-Dslushy.hold.required-age-hours=0 \
		-Dslushy.archiver.scan-period=30000 \
		-Dslushy.archiver.required-age-hours=0 \
		-Dslushy.withdraw.scan-period=30000

TAG := kemitix/slushy:$(shell git describe --tags)

docker:
	docker build . -t ${TAG}	
	# docker tag ${TAG} kemitix/slushy

run: docker
	docker run -it ${TAG}

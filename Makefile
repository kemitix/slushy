
install: .install

.install:
	mvn install
	touch .install

clean:
	mvn clean
	if [ -f .install ];then rm .install;fi

dev:
	mvn quarkus:dev -pl slushy



install: .install

.install:
	mvn install
	touch .install

clean:
	mvn clean
	rm .install

dev:
	mvn quarkus:dev -pl slushy


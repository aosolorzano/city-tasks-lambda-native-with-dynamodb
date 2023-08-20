
build-CityTasksEventsFunction:
	echo "Building CityTasksEventsFunction for 'dev' environment..."
	mvn -T 4C clean package -Pnative -DskipTests
	cp ./target/native $(ARTIFACTS_DIR)
	chmod 755 ./target/classes/bootstrap
	cp ./target/classes/bootstrap $(ARTIFACTS_DIR)

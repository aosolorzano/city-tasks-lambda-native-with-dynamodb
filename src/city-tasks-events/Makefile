
build-CityTasksEventsFunction:
	echo "Building CityTasksEventsFunction for 'dev' environment..."
	mvn clean package -Pnative -DskipTests
	cp ./target/native $(ARTIFACTS_DIR)
	chmod 755 ./shell/bootstrap
	cp ./shell/bootstrap $(ARTIFACTS_DIR)

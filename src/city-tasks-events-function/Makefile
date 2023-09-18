
build-CityTasksEventsFunction:
	echo "Building CityTasksEventsFunction for 'dev' environment..."
	mvn -T 4C clean native:compile -Pnative -DskipTests -Ddependency-check.skip=true
	cp ./target/native $(ARTIFACTS_DIR)
	chmod 755 ./utils/shell/bootstrap
	cp ./utils/shell/bootstrap $(ARTIFACTS_DIR)

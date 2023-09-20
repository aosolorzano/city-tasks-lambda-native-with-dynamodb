
build-CityTasksEventsFunction:
	echo "Building CityTasksEventsFunction for 'dev' environment..."
	mvn -T 4C clean native:compile -Pnative -DskipTests -f ./src/city-tasks-events-function/pom.xml -Ddependency-check.skip=true
	cp ./src/city-tasks-events-function/target/native $(ARTIFACTS_DIR)
	cp ./src/city-tasks-events-function/utils/shell/bootstrap $(ARTIFACTS_DIR)
	chmod 755 $(ARTIFACTS_DIR)/bootstrap

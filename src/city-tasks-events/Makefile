
build-CityTasksEventsFunction:
	echo "Building CityTasksEventsFunction for '$(HIPERIUM_CITY_ENV)' environment..."
	mvn -T 4C clean package -Pnative -DskipTests -Dhiperium.city.profile=$(HIPERIUM_CITY_ENV)
	cp ./target/native $(ARTIFACTS_DIR)
	chmod 755 ./target/classes/bootstrap
	cp ./target/classes/bootstrap $(ARTIFACTS_DIR)

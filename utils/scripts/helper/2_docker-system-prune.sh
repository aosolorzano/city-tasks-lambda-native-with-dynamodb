#!/usr/bin/env bash

echo ""
echo "PRUNING DOCKER SYSTEM..."
echo ""
docker system prune --all --force --volumes

### REMOVING ALL EXITED CONTAINERS
docker rm "$(docker ps -a -f status=exited -q)"

echo ""
echo "DONE!"

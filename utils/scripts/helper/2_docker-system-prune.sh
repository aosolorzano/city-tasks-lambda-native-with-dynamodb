#!/usr/bin/env bash

echo ""
echo "PRUNING DOCKER SYSTEM..."
echo ""
docker system prune --all --force --volumes
docker rm -f "$(docker ps -aq)"

echo ""
echo "DONE!"

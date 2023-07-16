#!/usr/bin/env bash

echo ""
echo "PRUNING DOCKER SYSTEM..."
echo ""
docker system prune --all --force --volumes

echo ""
echo "DONE!"

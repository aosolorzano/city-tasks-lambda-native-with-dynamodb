#!/bin/sh

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}

docker build -t al2-graalvm:maven -f utils/docker/sam-builder/Dockerfile .

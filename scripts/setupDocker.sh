#!/bin/bash

####################
# builds a Spark-container and runs it
####################

DOCKERMACHINE=parquet-demo
ROOTDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd ".." && pwd )"

# build containers
docker network create --driver bridge parquetdemonw
docker build -t $DOCKERMACHINE:latest $ROOTDIR/scripts/docker

# run container
docker run --net=parquetdemonw -d -h $DOCKERMACHINE --name $DOCKERMACHINE --net-alias $DOCKERMACHINE -p 5005:5005 -p 9000:9000 -p 4040:4040 -d $DOCKERMACHINE:latest

# output
echo "Spark UI available at http://172.19.0.2:8080/ (or whatever your container URL is)"  #TODO Find a way to get container IP
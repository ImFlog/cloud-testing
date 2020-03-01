#!/usr/bin/env sh

function cleanup {
    echo "Stopping container ... "
    docker kill ${docker_process}
}
trap cleanup EXIT

docker_process=$(docker container run -e SERVICES=dynamodb,sqs -p 4576:4576 -p 4569:4569 -d localstack/localstack:0.10.7)

sleep 2s

awslocal sqs create-queue --queue-name poke-queue
awslocal dynamodb create-table \
    --table-name poke-table \
    --attribute-definitions AttributeName=id,AttributeType=N \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

./gradlew bootRun --args='--spring.profiles.active=dev'

#!/usr/bin/env sh

function cleanup {
    echo "Stopping containers ... "
    docker-compose down
}
trap cleanup EXIT

docker-compose up -d

sleep 2s

# TODO : Create topics here using python ?


./gradlew bootRun --args='--spring.profiles.active=dev'
# TODO : CAN WE MOVE THIS TO GRADLE ?

# TODO: For tests, use https://github.com/nhartner/pubsub-emulator-demo/blob/master/src/test/java/nhartner/demo/pubsub/TestPubSubConfig.java

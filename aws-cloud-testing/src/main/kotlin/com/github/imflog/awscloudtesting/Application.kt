package com.github.imflog.awscloudtesting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(DynamoDBConfig::class, SqsConfig::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

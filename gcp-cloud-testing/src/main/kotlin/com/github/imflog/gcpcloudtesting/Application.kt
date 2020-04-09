package com.github.imflog.gcpcloudtesting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
//@EnableConfigurationProperties(DatastoreConfig::class, PubsubConfig::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
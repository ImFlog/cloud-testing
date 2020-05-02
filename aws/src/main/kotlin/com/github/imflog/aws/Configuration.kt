package com.github.imflog.aws

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
@ConfigurationProperties(prefix = "aws.dynamodb")
class DynamoDBConfig {
    lateinit var region: Regions
    lateinit var endpoint: String

    @Bean
    fun amazonDynamoDB(): AmazonDynamoDB = AmazonDynamoDBClient.builder()
        // We use the default credential toolchain
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region.getName()))
        .build()
}

@Configuration
@ConfigurationProperties(prefix = "aws.sqs")
class SqsConfig {
    lateinit var region: Regions
    lateinit var queue: String
    lateinit var endpoint: String

    @Bean
    fun sqsClient(): AmazonSQS = AmazonSQSClient.builder()
        // We use the default credential toolchain
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region.getName()))
        .build()

    @Bean("QUEUE_URL")
    fun queueUrl(sqsClient: AmazonSQS): String = sqsClient.getQueueUrl(queue).queueUrl
}

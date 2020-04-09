package com.github.imflog.gcpcloudtesting

import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.AlreadyExistsException
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.cloud.NoCredentials
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.KeyFactory
import com.google.cloud.pubsub.v1.Publisher
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings
import com.google.cloud.pubsub.v1.TopicAdminClient
import com.google.cloud.pubsub.v1.TopicAdminSettings
import com.google.pubsub.v1.ProjectTopicName
import io.grpc.ManagedChannelBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct


const val PROJECT_ID = "cloud-testing"

@Configuration
class DatastoreConfig {

    @Bean
    fun datastore(): Datastore = DatastoreOptions
        .newBuilder()
        // TODO : PUT THIS IN CONFIG FILES
        .setHost("localhost:8081")
        .setProjectId(PROJECT_ID)
        .setCredentials(NoCredentials.getInstance())
        .build()
        .service

    @Bean
    fun keyFactory(datastore: Datastore): KeyFactory = datastore.newKeyFactory()
}

// TODO: Move this in configuration ?
const val TOPIC_NAME = "poke-queue"

@Configuration
class PubsubConfig {

    private val pubsubEndpoint = "localhost:8085"
    // TODO : Do not let this internal
    internal val credentialsProvider = NoCredentialsProvider.create()

    // TODO : What about regular case ? Do not use it everytime
    // TODO : Understand why we need to use a channel provider and not an endpoint
    @Bean
    fun channelProvider(): FixedTransportChannelProvider =
        ManagedChannelBuilder.forTarget(pubsubEndpoint).usePlaintext().build().let {
            FixedTransportChannelProvider.create(GrpcTransportChannel.create(it))
        }

    @Bean
    fun publisher(channelProvider: FixedTransportChannelProvider): Publisher = Publisher
        .newBuilder(ProjectTopicName.of(PROJECT_ID, TOPIC_NAME))
        .setChannelProvider(channelProvider)
        // TODO : This should be conditionalish
        .setCredentialsProvider(credentialsProvider)
        .build()

    @Bean
    fun topicAdminClient(channelProvider: FixedTransportChannelProvider): TopicAdminClient = TopicAdminClient.create(
        TopicAdminSettings.newBuilder()
            .setTransportChannelProvider(channelProvider)
            .setCredentialsProvider(credentialsProvider).build()
    )

    @Bean
    fun subscriptionAdminClient(channelProvider: FixedTransportChannelProvider): SubscriptionAdminClient =
        SubscriptionAdminClient.create(
            SubscriptionAdminSettings.newBuilder()
                .setTransportChannelProvider(channelProvider)
                .setCredentialsProvider(credentialsProvider)
                .build()
        )

    @PostConstruct
    fun createTopic(topicAdminClient: TopicAdminClient) {
        val topic = ProjectTopicName.of(PROJECT_ID, TOPIC_NAME)
        try {
            topicAdminClient.createTopic(topic)
        } catch (e: AlreadyExistsException) {
            // this is fine, already created
            topicAdminClient.getTopic(topic)
        }
    }
}

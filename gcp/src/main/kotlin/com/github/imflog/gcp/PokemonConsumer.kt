package com.github.imflog.gcp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.rpc.AlreadyExistsException
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.cloud.pubsub.v1.Subscriber
import com.google.cloud.pubsub.v1.SubscriptionAdminClient
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.PushConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import kotlin.concurrent.thread


@Component
class PokemonConsumer(
    private val pokemonRepository: PokemonRepository,
    private val channelProvider: FixedTransportChannelProvider,
    private val subscriptionAdminClient: SubscriptionAdminClient,
    private val objectMapper: ObjectMapper
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PokemonConsumer::class.java)

        // TODO : Move this in configuration ?
        private const val SUBSCRIPTION_NAME = "poke-subscription"
    }

    @PostConstruct
    // TODO : This method is a bit too linked to configuration
    // TODO: Try to extract the Subscriber ...
    fun saveCaughtPokemonInfo() {
        // Do we need this here ?
        createSubscription()
        // ----------------------

        LOGGER.info("Starting consumption of caught pokemon")
        val subscriptionName = ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_NAME)
        thread {
            var subscriber: Subscriber? = null
            try {
                // TODO: Should we build the subscriber in the configuration ? Except for the function
                subscriber = Subscriber.newBuilder(subscriptionName, processMessage())
                    // TODO : PUT THIS IN CONFIG FILES
                    .setChannelProvider(channelProvider)
                    // TODO : This also should come from the configuration
                    .setCredentialsProvider(NoCredentialsProvider.create())
                    .build()

                subscriber.startAsync().awaitRunning()
                // Allow the subscriber to run indefinitely unless an unrecoverable error occurs
                subscriber.awaitTerminated()
            } finally {
                // Stop receiving messages
                subscriber?.stopAsync()
            }
        }
    }

    private fun processMessage(): MessageReceiver = MessageReceiver { message, _: AckReplyConsumer ->
        val pokemon = objectMapper.readValue<Pokemon>(message.data.toByteArray())
        pokemonRepository.insertPokemon(pokemon)
    }

    private fun createSubscription() {
        val topic = ProjectTopicName.of(PROJECT_ID, TOPIC_NAME)
        val subscription = ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_NAME)

        try {
            subscriptionAdminClient.createSubscription(subscription, topic, PushConfig.getDefaultInstance(), 100)
        } catch (e: AlreadyExistsException) {
            // this is fine, already created
        }
    }
}

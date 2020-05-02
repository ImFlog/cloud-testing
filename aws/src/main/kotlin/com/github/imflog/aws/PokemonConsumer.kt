package com.github.imflog.aws

import com.amazonaws.services.sqs.AmazonSQS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import kotlin.concurrent.thread

@Component
class PokemonConsumer(
    private val sqs: AmazonSQS,
    @Qualifier("QUEUE_URL") private val queueUrl: String,
    private val pokemonRepository: PokemonRepository,
    private val objectMapper: ObjectMapper
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PokemonConsumer::class.java)
    }

    @PostConstruct
    fun saveCaughtPokemonInfo() {
        LOGGER.info("Starting consumption of caught pokemon")
        thread {
            while (true) {
                sqs.receiveMessage(this.queueUrl).messages.asSequence()
                    .map { it.body }
                    .map { objectMapper.readValue<Pokemon>(it) }
                    .forEach { pokemonRepository.insertPokemon(it) }
                Thread.sleep(500)
            }
        }
    }
}

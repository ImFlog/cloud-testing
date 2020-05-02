package com.github.imflog.gcp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.cloud.pubsub.v1.Publisher
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PokemonProducer(
    private val publisher: Publisher,
    private val objectMapper: ObjectMapper
) {

    private val pokedex = initPokedex()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PokemonProducer::class.java)
    }

    @Scheduled(fixedDelay = 1_000)
    fun produceNewPokemon() {
        val pokemonToSend = pokedex.shuffled().first()
        LOGGER.info("New pokemon caught: ${pokemonToSend.name}")
        val pokeString = objectMapper.writeValueAsString(pokemonToSend)
        val pubSubMessage = PubsubMessage.newBuilder()
            .setData(ByteString.copyFromUtf8(pokeString))
            .build()
        publisher.publish(pubSubMessage)
    }

    private fun initPokedex(): List<Pokemon> {
        val bootstrapSchema = CsvSchema.emptySchema().withHeader()
        val mapper = CsvMapper().registerKotlinModule()
        val file = ClassPathResource("pokemon.csv").file
        return mapper.readerFor(Pokemon::class.java).with(bootstrapSchema).readValues<Pokemon>(file).readAll()
    }
}

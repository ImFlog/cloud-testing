package com.github.imflog.aws

import com.amazonaws.services.sqs.AmazonSQS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PokemonProducer(
    private val sqs: AmazonSQS,
    @Qualifier("QUEUE_URL") private val queueUrl: String,
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
        sqs.sendMessage(this.queueUrl, objectMapper.writeValueAsString(pokemonToSend))
    }

    private fun initPokedex(): List<Pokemon> {
        val bootstrapSchema = CsvSchema.emptySchema().withHeader()
        val mapper = CsvMapper().registerKotlinModule()
        val file = ClassPathResource("pokemon.csv").file
        return mapper.readerFor(Pokemon::class.java).with(bootstrapSchema).readValues<Pokemon>(file).readAll()
    }
}

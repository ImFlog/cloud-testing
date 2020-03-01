package com.github.imflog.awscloudtesting

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.ItemUtils
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Repository

@Repository
class PokemonRepository(
    private val dynamoDbClient: AmazonDynamoDB,
    private val objectMapper: ObjectMapper
) {
    companion object {

        private const val POKE_TABLE = "poke-table"

        inline fun <reified T> fromAttributeValue(item: Map<String, AttributeValue>, objectMapper: ObjectMapper): T {
            return objectMapper.readValue(ItemUtils.toItem(item).toJSON(), T::class.java)
        }

        fun toAttributeValues(obj: Any, objectMapper: ObjectMapper): Map<String, AttributeValue> {
            val json = objectMapper.writeValueAsString(obj)
            val item = Item().withJSON("document", json)
            val attributes = ItemUtils.toAttributeValues(item)
            return attributes["document"]?.m ?: mapOf()
        }

    }

    fun scan(): List<PokemonDto> {
        val scanResult = dynamoDbClient.scan(
            ScanRequest()
                .withTableName(POKE_TABLE)
                .withAttributesToGet("id", "name")
        ).items

        return scanResult.map { fromAttributeValue<PokemonDto>(it, objectMapper) }.sortedBy(PokemonDto::id)
    }

    fun findOne(id: Int): Pokemon {
        val request = GetItemRequest()
            .withTableName(POKE_TABLE)
            .withKey(mapOf("id" to AttributeValue().withN(id.toString())))
        return fromAttributeValue(dynamoDbClient.getItem(request).item, objectMapper)
    }

    fun insertPokemon(pokemon: Pokemon) {
        val putItemRequest = PutItemRequest()
            .withTableName(POKE_TABLE)
            .withItem(toAttributeValues(pokemon, objectMapper))
        dynamoDbClient.putItem(putItemRequest)
    }
}

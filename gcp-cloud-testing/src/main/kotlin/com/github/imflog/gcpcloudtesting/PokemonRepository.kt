package com.github.imflog.gcpcloudtesting

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.KeyFactory
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery
import org.springframework.stereotype.Repository

@Repository
// TODO: FOR SOME REASON IT SEEMS THAT THE DATASTORE CALLS FAILS, INVESTIGATE
class PokemonRepository(private val datastore: Datastore, private val keyFactory: KeyFactory) {

    companion object {
        private const val POKE_KIND = "poke-kind"
    }

    @ExperimentalStdlibApi
    fun scan(): List<PokemonDto> {
        val query = Query.newProjectionEntityQueryBuilder()
            .setKind(POKE_KIND)
            .setProjection(PokemonDto::id.name, PokemonDto::name.name)
            .setOrderBy(StructuredQuery.OrderBy.asc(PokemonDto::id.name))
            .build()
        val cursor = datastore.run(query)
        return buildList {
            while (cursor.hasNext()) {
                add(PokemonDto.fromEntity(cursor.next()))
            }
        }
    }

    fun findOne(id: Long): Pokemon {
        val query = Query.newEntityQueryBuilder()
            .setKind(POKE_KIND)
            .setFilter(
                StructuredQuery.PropertyFilter.eq(
                    "__key__",
                    keyFactory.setKind(POKE_KIND).newKey(id)
                )
            ).build()
        val cursor = datastore.run(query)
        return Pokemon.fromEntity(cursor.next())
    }

    fun insertPokemon(pokemon: Pokemon) {
        val pokeKey = keyFactory
            .setKind(POKE_KIND)
            .newKey(pokemon.id)

        // TODO : Don't really like this part ...
        val entity = Entity.newBuilder(pokeKey)
            .set(Pokemon::id.name, pokemon.id)
            .set(Pokemon::name.name, pokemon.name)
            .set(Pokemon::type1.name, pokemon.type1)
            .set(Pokemon::type2.name, pokemon.type2)
            .set(Pokemon::hp.name, pokemon.hp)
            .set(Pokemon::attack.name, pokemon.attack)
            .set(Pokemon::defense.name, pokemon.defense)
            .set(Pokemon::spAtk.name, pokemon.spAtk)
            .set(Pokemon::spDef.name, pokemon.spDef)
            .set(Pokemon::speed.name, pokemon.speed)
            .set(Pokemon::generation.name, pokemon.generation)
            .set(Pokemon::legendary.name, pokemon.legendary)
            .build()
        datastore.put(entity)
    }
}

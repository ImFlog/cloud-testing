package com.github.imflog.gcp

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.cloud.datastore.BaseEntity
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Pokemon(
    val id: Long,
    val name: String,
    val type1: String,
    val type2: String = "",
    val hp: Long,
    val attack: Long,
    val defense: Long,
    val spAtk: Long,
    val spDef: Long,
    val speed: Long,
    val generation: Long,
    val legendary: Boolean
) {
    companion object {
        // TODO : We could simplify this to be generic
        fun fromEntity(entity: BaseEntity<Key>) = Pokemon(
            entity.getLong(Pokemon::id.name),
            entity.getString(Pokemon::name.name),
            entity.getString(Pokemon::type1.name),
            entity.getString(Pokemon::type2.name),
            entity.getLong(Pokemon::hp.name),
            entity.getLong(Pokemon::attack.name),
            entity.getLong(Pokemon::defense.name),
            entity.getLong(Pokemon::spAtk.name),
            entity.getLong(Pokemon::spDef.name),
            entity.getLong(Pokemon::speed.name),
            entity.getLong(Pokemon::generation.name),
            entity.getBoolean(Pokemon::legendary.name)
        )
    }
}

data class PokemonDto(
    val id: Long,
    val name: String
) {
    companion object {
        // TODO : We could simplify this to be generic
        fun fromEntity(entity: BaseEntity<Key>) = PokemonDto(
            entity.getLong(PokemonDto::id.name),
            entity.getString(PokemonDto::name.name)
        )
    }
}

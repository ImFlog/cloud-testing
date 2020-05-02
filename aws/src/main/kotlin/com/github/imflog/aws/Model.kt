package com.github.imflog.aws

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Pokemon(
    val id: Int,
    val name: String,
    val type1: String,
    val type2: String = "",
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val spAtk: Int,
    val spDef: Int,
    val speed: Int,
    val generation: Int,
    val legendary: Boolean
)

data class PokemonDto(
    val id: Int,
    val name: String
)

package com.github.imflog.aws

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PokemonController(private val pokemonRepository: PokemonRepository) {

    @GetMapping("/pokemons")
    fun listPokemon(): List<PokemonDto> = pokemonRepository.scan()

    @GetMapping("/pokemons/{id}")
    fun getPokemonData(@PathVariable id: Int): Pokemon = pokemonRepository.findOne(id)
}

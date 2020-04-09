package com.github.imflog.gcpcloudtesting

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PokemonController(private val pokemonRepository: PokemonRepository) {

    @ExperimentalStdlibApi
    @GetMapping("/pokemons")
    // TODO: This cause an I/O error
    fun listPokemon(): List<PokemonDto> = pokemonRepository.scan()

    @GetMapping("/pokemons/{id}")
    fun getPokemonData(@PathVariable id: Long): Pokemon = pokemonRepository.findOne(id)
}

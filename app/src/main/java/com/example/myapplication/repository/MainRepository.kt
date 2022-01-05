package com.example.myapplication.repository

import com.example.myapplication.pokeapi.RetrofitService

class MainRepository constructor(private val retrofitService: RetrofitService){
    suspend fun getPokemonList() = retrofitService.getPokemonList()
    suspend fun getPokemonDetails(id: Int) = retrofitService.getPokemonDetails(id)
}

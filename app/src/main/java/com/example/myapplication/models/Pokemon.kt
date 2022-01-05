package com.example.myapplication.models

data class Pokemon(val name: String, val url: String, var types: ArrayList<String>, var abilities: Int, var height: Int, var weight: Int) {
    init {
        types = arrayListOf()
    }

    fun getPokemonNumber() : Int{
        val urlSlides = url.split("/")
        return Integer.parseInt(urlSlides[urlSlides.size - 2])
    }

    fun getPokemonTypes() = types.joinToString(separator = ", ")
}
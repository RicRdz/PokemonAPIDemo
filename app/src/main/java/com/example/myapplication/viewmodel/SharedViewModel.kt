package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.repository.MainRepository
import com.example.myapplication.models.Pokemon
import com.example.myapplication.models.PokemonsListResponse
import com.example.myapplication.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.*

class SharedViewModel constructor(private val mainRepository: MainRepository): ViewModel() {
    private var originalPokemonList = listOf<Pokemon>()
    val mutablePokemonList = MutableLiveData<List<Pokemon>>()
    val mutablePokemonTypesList = MutableLiveData<List<String>>()
    private var pokemonTypesList = mutableListOf<String>()
    private var currentPokemon = MutableLiveData<Pokemon>()
    val errorMessage = MutableLiveData<String>()
    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun filterPokemonList(filter: String) {
        when(filter) {
            Constants.ALL_TYPES -> mutablePokemonList.value = originalPokemonList
            else ->
                mutablePokemonList.value = originalPokemonList.filter { pokemon -> pokemon.types.contains(filter) }
        }
    }

    fun getAllPokemon() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = mainRepository.getPokemonList()
            pokemonTypesList.clear()
            pokemonTypesList.add(Constants.ALL_TYPES)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val list = (response.body() as PokemonsListResponse).results
                    originalPokemonList = list
                    mutablePokemonList.postValue(originalPokemonList)
                    list.forEach{ pokemon ->
                        val details = mainRepository.getPokemonDetails((pokemon).getPokemonNumber())
                        if (details.isSuccessful) {
                            val detailsBody = details.body() as JsonObject
                            val abilities = detailsBody.getAsJsonArray("abilities")
                            val abilitiesCount: Int = abilities.count()
                            pokemon.abilities = abilitiesCount
                            val height = detailsBody.get("height").toString()
                            pokemon.height = Integer.parseInt(height)
                            val weight = detailsBody.get("weight").toString()
                            pokemon.weight = Integer.parseInt(weight)
                            val types = detailsBody.getAsJsonArray("types")
                            pokemon.types = arrayListOf()
                            types.forEach{
                                val typeObject = it.asJsonObject
                                val type = typeObject.get("type") as JsonObject
                                val typeName = type.get("name").asString
                                pokemon.types.add(typeName)
                                if (!pokemonTypesList.contains(typeName)) pokemonTypesList.add(typeName)
                            }
                        }
                    }
                    mutablePokemonTypesList.postValue(pokemonTypesList)
                    loading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    fun setCurrentPokemon(pokemon: Pokemon) {
        currentPokemon.value = pokemon
    }

    fun getCurrentPokemon() : LiveData<Pokemon> =  currentPokemon

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
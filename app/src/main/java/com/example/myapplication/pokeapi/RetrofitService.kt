package com.example.myapplication.pokeapi

import com.example.myapplication.models.PokemonsListResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitService {
    @GET("pokemon?limit=150")
    suspend fun getPokemonList() : Response<PokemonsListResponse>

    @GET("pokemon/{id}")
    suspend fun getPokemonDetails(@Path("id") id : Int) : Response<JsonObject>

    companion object {
        private var retrofitService: RetrofitService? = null
        fun getInstance() : RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://pokeapi.co/api/v2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
    }


}
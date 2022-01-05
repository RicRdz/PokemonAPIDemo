package com.example.myapplication.models

import com.google.gson.annotations.SerializedName

data class PokemonsListResponse(val count: Int,
                                val next: String,
                                val previous: String,
                                val results: List<Pokemon>)
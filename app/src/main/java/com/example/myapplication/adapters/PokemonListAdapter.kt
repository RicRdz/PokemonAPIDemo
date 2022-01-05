package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.R
import com.example.myapplication.models.Pokemon
import com.example.myapplication.utils.PokemonsDiffUtil

class PokemonListAdapter(private val onClick: (Pokemon) -> Unit) : ListAdapter<Pokemon, PokemonListAdapter.PokemonViewHolder>(PokemonsDiffUtil) {

    class PokemonViewHolder(itemView: View, val onClick: (Pokemon) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private var currentPokemon: Pokemon? = null
        private var pictureImageView: ImageView = itemView.findViewById(R.id.pokemon_picture)
        private var nameTextView: TextView = itemView.findViewById(R.id.pokemon_name)
        init {
            itemView.setOnClickListener {
                currentPokemon?.let {
                    onClick(it)
                }
            }
        }

        fun bind(pokemon: Pokemon) {
            currentPokemon = pokemon
            Glide.with(pictureImageView.context).load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon.getPokemonNumber() + ".png").placeholder(R.drawable.ic_launcher_foreground).centerCrop().diskCacheStrategy(
                DiskCacheStrategy.ALL).into(pictureImageView)
            nameTextView.text = pokemon.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pokemon_list_row, parent,false)
        return PokemonViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = getItem(position)
        holder.bind(pokemon)
    }

}
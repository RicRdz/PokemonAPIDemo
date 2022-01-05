package com.example.myapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myapplication.repository.MainRepository
import com.example.myapplication.R
import com.example.myapplication.viewmodel.SharedViewModel
import com.example.myapplication.viewmodel.ViewModelFactory
import com.example.myapplication.models.Pokemon
import com.example.myapplication.pokeapi.RetrofitService

class DetailsFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var navController : NavController
    private var pokemon: Pokemon? = null
    private var nameTextView: TextView? = null
    private var characterImageView: ImageView? = null
    private var idTextView: TextView? = null
    private var abilitiesTextView: TextView? = null
    private var heightTextView: TextView? = null
    private var weightTextView: TextView? = null
    private var typesTextView: TextView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Details"
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)
        sharedViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(mainRepository)).get(
            SharedViewModel::class.java)

        pokemon = sharedViewModel.getCurrentPokemon().value
        nameTextView = view.findViewById(R.id.name)

        nameTextView?.text = pokemon?.name
        characterImageView = view.findViewById(R.id.character_image)
        characterImageView?.context?.let {
            Glide.with(it).load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemon?.getPokemonNumber() + ".png").placeholder(
                R.drawable.ic_launcher_foreground
            ).centerCrop().diskCacheStrategy(
                DiskCacheStrategy.ALL).into(characterImageView!!)
        }

        idTextView = view.findViewById(R.id.idData)
        idTextView?.text = pokemon?.getPokemonNumber().toString()

        abilitiesTextView = view.findViewById(R.id.abilitiesData)
        abilitiesTextView?.text = pokemon?.abilities.toString()

        heightTextView = view.findViewById(R.id.heightData)
        heightTextView?.text = pokemon?.height.toString()

        weightTextView = view.findViewById(R.id.weightData)
        weightTextView?.text = pokemon?.weight.toString()

        typesTextView = view.findViewById(R.id.typesData)
        typesTextView?.text = pokemon?.getPokemonTypes()
    }
}
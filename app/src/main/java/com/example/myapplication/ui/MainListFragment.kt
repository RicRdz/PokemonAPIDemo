package com.example.myapplication.ui

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapters.PokemonListAdapter
import com.example.myapplication.pokeapi.RetrofitService
import androidx.appcompat.app.AppCompatActivity

import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.FragmentTransaction
import com.example.myapplication.repository.MainRepository
import com.example.myapplication.R
import com.example.myapplication.viewmodel.SharedViewModel
import com.example.myapplication.viewmodel.ViewModelFactory


class MainListFragment : Fragment() {
    lateinit var navController: NavController
    lateinit var recyclerView: RecyclerView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var categorySelector: Spinner
    private lateinit var loadingDialog: Dialog
    private lateinit var loadingView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        loadingDialog = Dialog(requireActivity(), android.R.style.Theme_Translucent_NoTitleBar)
        loadingView = requireActivity().layoutInflater.inflate(R.layout.full_screen_progress_bar, null)
        loadingDialog.setContentView(loadingView)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)
        sharedViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(mainRepository)).get(
            SharedViewModel::class.java)
        sharedViewModel.getAllPokemon()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Pokemon API Demo"
        return inflater.inflate(R.layout.fragment_main_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        recyclerView = view.findViewById(R.id.pokemons_list)
        recyclerView.adapter = PokemonListAdapter {
            sharedViewModel.setCurrentPokemon(it)
            navController.navigate(R.id.action_mainListFragment_to_detailsFragment)
        }

        sharedViewModel.mutablePokemonList.observe(requireActivity(), {
            (recyclerView.adapter as PokemonListAdapter).submitList(it)
        })

        sharedViewModel.errorMessage.observe(requireActivity(), {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_LONG).show()
        })

        sharedViewModel.loading.observe(requireActivity(),{
            if (it == false) loadingDialog.dismiss() else loadingDialog.show()
        })

        categorySelector = view.findViewById(R.id.category_selector)

        sharedViewModel.mutablePokemonTypesList.observe(requireActivity(), {
            val adapter = ArrayAdapter(requireActivity(), R.layout.spinner_item, it)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySelector.adapter = adapter
        })

        categorySelector.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                sharedViewModel.filterPokemonList(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about -> {
                val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                val aboutFragment = AboutFragment()
                ft.replace(R.id.nav_host_ragment, aboutFragment)
                ft.addToBackStack(null)
                ft.commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
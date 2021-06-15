package com.magentagang.apellai.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentCardAlbumHScrollBinding
import com.magentagang.apellai.databinding.FragmentSearchBinding
import com.magentagang.apellai.viewmodel.CardAlbumHScrollViewModel
import com.magentagang.apellai.viewmodel.factory.CardAlbumHScrollViewModelFactory
import timber.log.Timber

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val viewModelFactory = SearchViewModelFactory(application)
        val searchViewModel = ViewModelProvider(this, viewModelFactory).get(
            SearchViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_search, container, false
        )

        binding.searchViewModel = searchViewModel
        binding.lifecycleOwner = this

        searchViewModel.subsonicResponseRoot.observe(viewLifecycleOwner, {
            //Timber.i("ALBUMS SEARCH -> ${it.toString()}")
            if(it != null){
                Timber.i("Search Result -> ${it.subsonicResponse.searchResult3?.album.toString() ?: "searchResult3 is null"}")
            }else{
                Timber.i("Search Result -> subsonicResponseRoot is null")
            }
        })

        binding.searchView.onQueryTextChanged {
            Timber.i("Search View Text: $it")
            searchViewModel.searchQuery.value = it
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }
}
package com.magentagang.apellai.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.magentagang.apellai.R
import com.magentagang.apellai.adapter.*
import com.magentagang.apellai.databinding.FragmentSearchBinding
import com.magentagang.apellai.model.Album
import com.magentagang.apellai.model.Artist
import com.magentagang.apellai.ui.artistscreen.ArtistScreenDirections
import com.magentagang.apellai.ui.library.LibraryFragmentDirections
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

        val adapterAlbum = ListAlbumAdapter(AlbumListener { ID ->
            searchViewModel.onAlbumClicked(ID)
        })
        val adapterArtist = ListArtistAdapter(ArtistListener { ID ->
            searchViewModel.onArtistClicked(ID)
        })
        val adapterTrack = ListTrackAdapter(TrackListener { ID ->
            searchViewModel.onArtistClicked(ID)
        })


        binding.searchResult.adapter = adapterAlbum

        searchViewModel.albums.observe(viewLifecycleOwner, {
            it?.let {
                adapterAlbum.submitList(it)
            }
        })
        searchViewModel.artists.observe(viewLifecycleOwner, {
            it?.let {
                adapterArtist.submitList(it)
            }
        })

        searchViewModel.navigateToAlbumScreen.observe(viewLifecycleOwner, { id ->
            id?.let {
                val navController = this.findNavController()
                navController.navigate(SearchFragmentDirections.actionNavigationSearchToAlbumScreen(id))
                searchViewModel.doneNavigating()
            }
        })

        searchViewModel.subsonicResponseRoot.observe(viewLifecycleOwner, {
            //Timber.i("ALBUMS SEARCH -> ${it.toString()}")
            if(it != null){
                Timber.i("Search Result -> ${it.subsonicResponse.searchResult3?.album.toString() ?: "searchResult3 is null"}")
                val chipChecked = binding.chipGroup.checkedChipId
                adapterAlbum.submitList(it.subsonicResponse.searchResult3?.album)
                adapterArtist.submitList(it.subsonicResponse.searchResult3?.artist)
                adapterTrack.submitList(it.subsonicResponse.searchResult3?.song)
                //TODO BUTUM HALA ETAR NAM SONG DISOS KEN TRACK HOBE
                when(chipChecked)
                {
                    binding.chipAlbum.id -> adapterAlbum.notifyDataSetChanged()
                    binding.chipArtist.id -> adapterArtist.notifyDataSetChanged()
                    binding.chipTrack.id -> adapterTrack.notifyDataSetChanged()
                }

            }else{
                Timber.i("Search Result -> subsonicResponseRoot is null")
            }
        })

        binding.chipAlbum.setOnClickListener {
            binding.searchResult.adapter = adapterAlbum
            adapterAlbum.notifyDataSetChanged()
        }
        binding.chipArtist.setOnClickListener {
            binding.searchResult.adapter = adapterArtist
            adapterArtist.notifyDataSetChanged()

        }
        binding.chipTrack.setOnClickListener {
            binding.searchResult.adapter = adapterTrack
            adapterTrack.notifyDataSetChanged()
        }

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
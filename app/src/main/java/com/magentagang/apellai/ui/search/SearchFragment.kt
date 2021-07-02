package com.magentagang.apellai.ui.search

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.magentagang.apellai.MainActivity
import com.magentagang.apellai.R
import com.magentagang.apellai.adapter.*
import com.magentagang.apellai.databinding.FragmentSearchBinding
import com.magentagang.apellai.repository.service.MediaSource
import com.magentagang.apellai.repository.service.PlaybackService
import com.magentagang.apellai.repository.service.PlaybackServiceConnector
import com.magentagang.apellai.ui.nowplayingscreen.NowPlayingViewModel
import com.magentagang.apellai.ui.nowplayingscreen.NowPlayingViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@ExperimentalCoroutinesApi
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val application = requireNotNull(this.activity).application
        val viewModelFactory = SearchViewModelFactory(application)
        val searchViewModel = ViewModelProvider(this, viewModelFactory).get(
            SearchViewModel::class.java
        )
        val playbackServiceConnector = PlaybackServiceConnector
            .getInstance(
                requireContext(),
                ComponentName(requireContext(), PlaybackService::class.java)
            )
        val nowPlayingViewModelFactory =
            NowPlayingViewModelFactory(playbackServiceConnector, application)
        val nowPlayingViewModel = ViewModelProvider(this, nowPlayingViewModelFactory).get(
            NowPlayingViewModel::class.java
        )
        val mediaSource = MediaSource.getInstance()

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
        val adapterTrack = ListTrackSearchAdapter(TrackListener { ID ->
            searchViewModel.tracks.value?.let {
                mediaSource.storeTracks(searchViewModel.tracks.value!!)
            }
            nowPlayingViewModel.playTrack(ID)
            MainActivity.showNowPlayingMini.postValue(true)
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
        searchViewModel.tracks.observe(viewLifecycleOwner, {
            it?.let {
                adapterTrack.submitList(it)
            }
        })

        searchViewModel.navigateToAlbumScreen.observe(viewLifecycleOwner, { id ->
            id?.let {
                val navController = this.findNavController()
                navController.navigate(
                    SearchFragmentDirections.actionNavigationSearchToAlbumScreen(
                        id
                    )
                )
                searchViewModel.doneNavigating()
            }
        })
        searchViewModel.navigateToArtistScreen.observe(viewLifecycleOwner, { id ->
            id?.let {
                val navController = this.findNavController()
                navController.navigate(
                    SearchFragmentDirections.actionNavigationSearchToArtistScreen(
                        id
                    )
                )
                searchViewModel.doneNavigating()
            }
        })

        searchViewModel.subsonicResponseRoot.observe(viewLifecycleOwner, {
            //Timber.i("ALBUMS SEARCH -> ${it.toString()}")
            if (it != null) {
                Timber.i("Search Result -> ${it.subsonicResponse.searchResult3?.album.toString()}")
                val chipChecked = binding.chipGroup.checkedChipId

                searchViewModel.albums.postValue(it.subsonicResponse.searchResult3?.album)
                searchViewModel.artists.postValue(it.subsonicResponse.searchResult3?.artist)
                searchViewModel.tracks.postValue(it.subsonicResponse.searchResult3?.song)

                //TODO BUTUM HALA ETAR NAM SONG DISOS KEN TRACK HOBE
                when (chipChecked) {
                    binding.chipAlbum.id -> adapterAlbum.notifyDataSetChanged()
                    binding.chipArtist.id -> adapterArtist.notifyDataSetChanged()
                    binding.chipTrack.id -> adapterTrack.notifyDataSetChanged()
                }

            } else {
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

}
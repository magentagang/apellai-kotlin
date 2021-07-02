package com.magentagang.apellai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.magentagang.apellai.R
import com.magentagang.apellai.adapter.AlbumListener
import com.magentagang.apellai.adapter.ListAlbumAdapter
import com.magentagang.apellai.databinding.FragmentListAlbumVScrollBinding
import com.magentagang.apellai.ui.artistscreen.ArtistScreenDirections
import com.magentagang.apellai.ui.library.LibraryFragmentDirections
import com.magentagang.apellai.viewmodel.ListAlbumViewModel
import com.magentagang.apellai.viewmodel.factory.ListAlbumViewModelFactory

class ListAlbumVScroll : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentListAlbumVScrollBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list_album_v_scroll, container, false
        )
        val albumListType = container?.tag.toString()
        val application = requireNotNull(this.activity).application
        val viewModelFactory = ListAlbumViewModelFactory(application, albumListType)
        val listAlbumViewModel =
            ViewModelProvider(this, viewModelFactory).get(ListAlbumViewModel::class.java)
        binding.listAlbumViewModel = listAlbumViewModel
        binding.lifecycleOwner = this
        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.albumVList.layoutManager = manager

        val adapter = ListAlbumAdapter(AlbumListener { ID ->
            listAlbumViewModel.onAlbumClicked(ID)
        })

        listAlbumViewModel.navigateToAlbumScreen.observe(viewLifecycleOwner, { id ->
            id?.let {
                val navController = this.findNavController()
                when (navController.currentDestination?.id) {
                    R.id.navigation_library -> navController.navigate(
                        LibraryFragmentDirections.actionNavigationLibraryToAlbumScreen(
                            id
                        )
                    )
                    R.id.artistScreen -> navController.navigate(
                        ArtistScreenDirections.actionArtistScreenToAlbumScreen(
                            id
                        )
                    )
                }
                listAlbumViewModel.doneNavigating()
            }
        })


        binding.albumVList.adapter = adapter

        listAlbumViewModel.albums.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}
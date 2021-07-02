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
import com.magentagang.apellai.adapter.ArtistListener
import com.magentagang.apellai.adapter.ListArtistAdapter
import com.magentagang.apellai.databinding.FragmentListArtistVScrollBinding
import com.magentagang.apellai.ui.library.LibraryFragmentDirections
import com.magentagang.apellai.viewmodel.ListArtistViewModel
import com.magentagang.apellai.viewmodel.factory.ListArtistViewModelFactory

class ListArtistVScroll : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentListArtistVScrollBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list_artist_v_scroll, container, false
        )

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ListArtistViewModelFactory(application)
        val listArtistViewModel =
            ViewModelProvider(this, viewModelFactory).get(ListArtistViewModel::class.java)

        binding.listArtistViewModel = listArtistViewModel
        binding.lifecycleOwner = this

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.artistVList.layoutManager = manager

        val adapter = ListArtistAdapter(ArtistListener { ID ->
            listArtistViewModel.onArtistClicked(ID)
        })

        listArtistViewModel.navigateToArtistScreen.observe(viewLifecycleOwner, { id ->
            id?.let {
                this.findNavController()
                    .navigate(LibraryFragmentDirections.actionNavigationLibraryToArtistScreen(id))
                listArtistViewModel.doneNavigating()
            }
        })

        binding.artistVList.adapter = adapter

        listArtistViewModel.artists.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}
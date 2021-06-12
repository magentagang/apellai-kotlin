package com.magentagang.apellai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.magentagang.apellai.R
import com.magentagang.apellai.adapter.CardAlbumHScrollAdapter
import com.magentagang.apellai.databinding.FragmentCardAlbumHScrollBinding
import com.magentagang.apellai.viewmodel.CardAlbumHScrollViewModel
import com.magentagang.apellai.viewmodel.factory.CardAlbumHScrollViewModelFactory
import timber.log.Timber

class CardAlbumHScroll : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentCardAlbumHScrollBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_card_album_h_scroll, container, false
        )
        val albumListType = container?.tag.toString()

        val application = requireNotNull(this.activity).application
        val viewModelFactory = CardAlbumHScrollViewModelFactory(application, albumListType)
        val cardAlbumHScrollViewModel = ViewModelProvider(this, viewModelFactory).get(CardAlbumHScrollViewModel::class.java)

        binding.categoryHeader.text = albumListType
        binding.cardAlbumHScrollViewModel = cardAlbumHScrollViewModel
        binding.lifecycleOwner = this

        val manager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.albumHList.layoutManager = manager

        val adapter = CardAlbumHScrollAdapter()
        binding.albumHList.adapter = adapter

        cardAlbumHScrollViewModel.albums.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}
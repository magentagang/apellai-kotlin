package com.magentagang.apellai.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.magentagang.apellai.R
import com.magentagang.apellai.viewmodel.ArtistScreenViewModel

class ArtistScreen : Fragment() {

    companion object {
        fun newInstance() = ArtistScreen()
    }

    private lateinit var viewModel: ArtistScreenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_artist_screen, container, false)
    }
}
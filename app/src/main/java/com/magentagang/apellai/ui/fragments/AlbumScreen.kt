package com.magentagang.apellai.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.magentagang.apellai.R
import com.magentagang.apellai.viewmodel.AlbumScreenViewModel

class AlbumScreen : Fragment() {

    companion object {
        fun newInstance() = AlbumScreen()
    }

    private lateinit var viewModel: AlbumScreenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album_screen, container, false)
    }
}
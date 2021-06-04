package com.magentagang.apellai.ui.albumscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.R
import com.magentagang.apellai.ui.home.HomeViewModel

class AlbumScreen : Fragment() {

    companion object {
        fun newInstance() = AlbumScreen()
    }
    //I added it again this time
    private lateinit var viewModel: AlbumScreenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album_screen, container, false)
    }
}

//TODO same library
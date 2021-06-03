package com.magentagang.apellai.ui.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.magentagang.apellai.R
import com.magentagang.apellai.viewmodel.ListArtistViewModel

class ListArtistVScroll : Fragment() {

    companion object {
        fun newInstance() = ListArtistVScroll()
    }

    private lateinit var viewModel: ListArtistViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_artist_v_scroll, container, false)
    }
}
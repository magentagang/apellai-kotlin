package com.magentagang.apellai.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.R

class LibraryFragment : Fragment() {

  private lateinit var libraryViewModel: LibraryViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    libraryViewModel =
            ViewModelProvider(this).get(LibraryViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_library, container, false)
    val textView: TextView = root.findViewById(R.id.text_library)
    libraryViewModel.text.observe(viewLifecycleOwner, {
      textView.text = it
    })
    return root
  }
}
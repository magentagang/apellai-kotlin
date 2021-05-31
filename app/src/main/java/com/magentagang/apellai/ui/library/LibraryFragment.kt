package com.magentagang.apellai.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentLibraryBinding
import com.magentagang.apellai.databinding.FragmentListAlbumVScrollBinding

class LibraryFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var libraryViewModel: LibraryViewModel

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val value = parent.getItemAtPosition(pos)
        Toast.makeText(context, "$value Selected", Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        libraryViewModel =
                ViewModelProvider(this).get(LibraryViewModel::class.java)
        val binding: FragmentLibraryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_library, container, false
        )
        val spinner = binding.spinnerLibrary
        this.activity?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.spinner_library_array,
                R.layout.spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }
        spinner.onItemSelectedListener = this
        return binding.root
        //return inflater.inflate(R.layout.fragment_library, container, false)
    }
}
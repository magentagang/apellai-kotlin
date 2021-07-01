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
import com.magentagang.apellai.ui.fragments.ListAlbumVScroll
import com.magentagang.apellai.ui.fragments.ListArtistVScroll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LibraryFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var binding: FragmentLibraryBinding

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val value = parent.getItemAtPosition(pos)
        val childFragmentManager = childFragmentManager
        val fragmentTransaction = childFragmentManager.beginTransaction()
        if (value == getString(R.string.artist)) {
            fragmentTransaction.replace(R.id.entityList, ListArtistVScroll()).commit()
        }
        else {
            fragmentTransaction.replace(R.id.entityList, ListAlbumVScroll()).commit()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(this.activity).application
        val viewModelFactory = LibraryViewModelFactory(application)
        libraryViewModel = ViewModelProvider(this, viewModelFactory).get(
            LibraryViewModel::class.java
        )
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_library, container, false
        )
        binding.libraryViewModel = libraryViewModel
        binding.lifecycleOwner = this

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

        binding.swipeContainer.setOnRefreshListener {
            Toast.makeText(application, "Refreshing library", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                val boolDeferred = libraryViewModel.initializeLibraryAsync()
                try {
                    val bool = boolDeferred.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                binding.swipeContainer.isRefreshing = false
            }
        }

        return binding.root
    }
}
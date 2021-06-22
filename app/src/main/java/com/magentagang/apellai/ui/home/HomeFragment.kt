package com.magentagang.apellai.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(this.activity).application
        val viewModelFactory = HomeViewModelFactory(application)
        val homeViewModel = ViewModelProvider(this, viewModelFactory).get(
            HomeViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        binding.homeViewModel = homeViewModel
        binding.lifecycleOwner = this
        binding.overflowMenuButton.setOnClickListener { showOverflow() }

        binding.swipeContainer.setOnRefreshListener {
            Toast.makeText(application, "Refreshing layout", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                val boolDeferred = homeViewModel.initializeCategories()
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

    private fun showOverflow() {
        PopupMenu(context, binding.overflowMenuButton).apply {
            setOnMenuItemClickListener(this@HomeFragment)
            inflate(R.menu.overflow_menu)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.logout_item -> {
                // TODO Log out
                true
            }
            else -> false
        }
    }
}
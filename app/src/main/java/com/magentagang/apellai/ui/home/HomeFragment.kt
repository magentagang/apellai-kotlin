package com.magentagang.apellai.ui.home

import android.app.Application
import android.content.Intent
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
import com.magentagang.apellai.LoginActivity
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel : HomeViewModel
    private lateinit var application : Application

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        application = requireNotNull(this.activity).application
        val viewModelFactory = HomeViewModelFactory(application)
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(
            HomeViewModel::class.java
        )

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
                homeViewModel.logOutUser()
                Toast.makeText(application, "Logging Out", Toast.LENGTH_SHORT).show()
                val mainIntent = Intent(activity, LoginActivity::class.java)
                mainIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainIntent)
                true
            }
            else -> false
        }
    }
}
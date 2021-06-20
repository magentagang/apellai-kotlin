package com.magentagang.apellai.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentLoginScreenBinding

class LoginScreenFragment : Fragment() {
    private lateinit var loginScreenViewModel: LoginScreenViewModel
    private lateinit var binding: FragmentLoginScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val viewModelFactory = LoginScreenViewModelFactory(application)
        val loginScreenViewModel = ViewModelProvider(this, viewModelFactory).get(
            LoginScreenViewModel::class.java
        )
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login_screen, container, false
        )
        binding.loginScreenViewModel = loginScreenViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }
}
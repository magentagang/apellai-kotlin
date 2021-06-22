package com.magentagang.apellai.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magentagang.apellai.MainActivity
import com.magentagang.apellai.R
import com.magentagang.apellai.databinding.FragmentLoginScreenBinding
import timber.log.Timber


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
        Toast.makeText(application,"Logging Screen Created Successfully",Toast.LENGTH_LONG).show()

        // TODO(Put authentication logic here)
        /*
            - If auth is successful, save & navigate
            - If auth is unsuccessful, show toast, clear text fields
         */
        binding.loginButton.setOnClickListener {
            // FIXME(Even though server address is sent, logic is implemented for one server only)
            Timber.i("Login Button Clicked: ${binding.serverAddress.text}, ${binding.username.text}, ${binding.password.text}")
            loginScreenViewModel.storeCredentialsOrToast(binding.serverAddress, binding.username, binding.password)
        }

        loginScreenViewModel.navigateToHomeScreen.observe(viewLifecycleOwner, { it ->
            it?.let {
                if (it == true) {
                    Toast.makeText(application,"Logged in successfully",Toast.LENGTH_LONG).show()
                    //TODO(Just navigate from here to home screen safely)
                    val mainIntent = Intent(activity, MainActivity::class.java)
                    startActivity(mainIntent)
                    loginScreenViewModel.setNavigateBooleanToNull()
                }else if(!it){
                    Toast.makeText(application,"Incorrect Credentials",Toast.LENGTH_LONG).show()
                    binding.password.text.clear()
                    loginScreenViewModel.setNavigateBooleanToNull()
                }
            }
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }
}
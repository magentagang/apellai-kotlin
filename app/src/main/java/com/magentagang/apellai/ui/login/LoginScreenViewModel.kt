package com.magentagang.apellai.ui.login

import android.app.Application
import android.widget.EditText
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.magentagang.apellai.model.SubsonicResponseRoot
import com.magentagang.apellai.model.User
import com.magentagang.apellai.repository.database.DatabaseDao
import com.magentagang.apellai.repository.database.UserDatabase
import com.magentagang.apellai.util.Constants
import com.magentagang.apellai.util.RepositoryUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class LoginScreenViewModel(application: Application): AndroidViewModel(application) {
    var databaseDao: DatabaseDao = UserDatabase.getInstance(application).databaseDao()
    var repositoryUtils: RepositoryUtils
    init{
        repositoryUtils = RepositoryUtils(databaseDao)
    }

    fun storeCredentialsOrToast(serverAddress: EditText, username: EditText, password: EditText) {
        val _username = username.text.toString()
        val _password = password.text.toString()
        CoroutineScope(Dispatchers.IO).launch{
            val authDeferred = repositoryUtils.authenticate(_username, _password)
            try{
                val authActual = authDeferred.await()
                if(authActual)
                {
                    val newUser = User(name = _username, password = _password, salt = Constants.SALT,
                        token = Constants.TOKEN, isActive = true)
                    databaseDao.resetUser()
                    databaseDao.insertUser(newUser)
                    _navigateToHomeScreen.postValue(true)
                }else{
                    _navigateToHomeScreen.postValue(false)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }

    private val _navigateToHomeScreen: MutableLiveData<Boolean?> = MutableLiveData<Boolean?>(null)
    val navigateToHomeScreen
        get() = _navigateToHomeScreen

    fun setNavigateBooleanToNull(){
        _navigateToHomeScreen.value = null
    }

}
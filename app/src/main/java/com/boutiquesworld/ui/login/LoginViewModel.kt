package com.boutiquesworld.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.repository.RetailerRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@FragmentScoped
class LoginViewModel @Inject constructor(private val retailerRepository: RetailerRepository) :
    ViewModel() {
    private val isEmailEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isPasswordEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isLoginSuccessful: MutableLiveData<Boolean> = MutableLiveData()

    fun loginRetailer(mobile: String, password: String) {
        isEmailEmpty.value = mobile.isEmpty()
        isPasswordEmpty.value = password.isEmpty()

        if (isEmailEmpty.value!! || isPasswordEmpty.value!!)
            return

        viewModelScope.launch {
            isLoginSuccessful.value = retailerRepository.loginRetailer(mobile, password)
        }
    }

    fun getIsEmailEmpty(): LiveData<Boolean> = isEmailEmpty

    fun getIsPasswordEmpty(): LiveData<Boolean> = isPasswordEmpty

    fun getIsLoginSuccessful(): LiveData<Boolean> = isLoginSuccessful
}
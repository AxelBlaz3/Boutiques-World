package `in`.trendition.ui.login

import `in`.trendition.repository.ProfileRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@FragmentScoped
class LoginViewModel @Inject constructor(private val profileRepository: ProfileRepository) :
    ViewModel() {
    private val isEmailEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isPasswordEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isLoginSuccessful: MutableLiveData<Boolean> = MutableLiveData()

    fun loginRetailer(mobile: String, password: String, loginType: Char) {
        isEmailEmpty.value = mobile.isEmpty()
        isPasswordEmpty.value = password.isEmpty()

        if (isEmailEmpty.value!! || isPasswordEmpty.value!!)
            return

        viewModelScope.launch {
            isLoginSuccessful.value = profileRepository.loginRetailer(mobile, password, loginType)
        }
    }

    fun getIsEmailEmpty(): LiveData<Boolean> = isEmailEmpty

    fun getIsPasswordEmpty(): LiveData<Boolean> = isPasswordEmpty

    fun getIsLoginSuccessful(): LiveData<Boolean> = isLoginSuccessful
}
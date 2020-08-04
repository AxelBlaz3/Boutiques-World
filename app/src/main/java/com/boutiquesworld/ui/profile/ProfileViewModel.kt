package com.boutiquesworld.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.model.Retailer
import com.boutiquesworld.repository.RetailerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileViewModel @Inject constructor(private val retailerRepository: RetailerRepository) :
    ViewModel() {
    private val retailer: MutableLiveData<Retailer> = MutableLiveData()

    init {
        viewModelScope.launch {
            retailer.value = retailerRepository.getRetailer()[0]
        }
    }

    fun getRetailer(): LiveData<Retailer> = retailer
}
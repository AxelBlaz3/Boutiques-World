package com.boutiquesworld.ui.product

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.model.Product
import com.boutiquesworld.repository.ProductRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    init {
        viewModelScope.launch {
            Log.d(this@ProductsViewModel.javaClass.simpleName, "Calling getProductsForBusiness()")
            productRepository.getProductsForBusiness(2) // TODO: Remove hardcoded businessId
        }
    }

    private val products: MutableLiveData<ArrayList<Product>> =
        productRepository.getProductsLiveData()

    fun getProductsLiveData(): LiveData<ArrayList<Product>> = products
}
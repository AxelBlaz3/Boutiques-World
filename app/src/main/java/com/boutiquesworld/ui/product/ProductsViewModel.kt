package com.boutiquesworld.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boutiquesworld.data.ProductRepository
import com.boutiquesworld.model.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {
    private val products: MutableLiveData<ArrayList<Product>> =
        productRepository.getProductsLiveData()

    fun getProductsLiveData(): LiveData<ArrayList<Product>> = products
}
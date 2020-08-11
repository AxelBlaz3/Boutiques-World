package com.boutiquesworld.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.model.Product
import com.boutiquesworld.model.Retailer
import com.boutiquesworld.repository.ProductRepository
import com.boutiquesworld.repository.RetailerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ViewModel for ProductsFragment.
 */
@Singleton
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val retailerRepository: RetailerRepository
) :
    ViewModel() {
    private val retailer: MutableLiveData<Retailer> =
        retailerRepository.getRetailerMutableLiveData()

    init {
        viewModelScope.launch {
            retailerRepository.updateRetailer()
            getProducts(forceRefresh = false)
        }
    }

    private val products: MutableLiveData<ArrayList<Product>> =
        productRepository.getProductsLiveData()

    fun getProductsLiveData(): LiveData<ArrayList<Product>> = products

    fun getProducts(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                productRepository.getProductsForBusiness(it.shopId, forceRefresh)
            }
        }
    }
}

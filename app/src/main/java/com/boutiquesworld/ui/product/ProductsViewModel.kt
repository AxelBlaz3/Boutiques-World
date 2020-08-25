package com.boutiquesworld.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.model.BaseProduct
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
            getFabrics(forceRefresh = false)
        }
    }

    private val products: MutableLiveData<ArrayList<BaseProduct>> =
        productRepository.getProductsLiveData()
    private val fabrics: MutableLiveData<ArrayList<BaseProduct>> =
        productRepository.getFabricsLiveData()

    fun getProductsLiveData(): LiveData<ArrayList<BaseProduct>> = products
    fun getFabricsLiveData(): LiveData<ArrayList<BaseProduct>> = fabrics

    fun getProducts(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                productRepository.getProductsForBusiness(it.shopId, forceRefresh)
            }
        }
    }

    fun getFabrics(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                productRepository.getFabrics(forceRefresh)
            }
        }
    }

    fun updateFabric(productId: Int, quantity: Int) {
        viewModelScope.launch {
            productRepository.updateFabric(productId, quantity)
        }
    }
}

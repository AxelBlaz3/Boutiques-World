package com.boutiquesworld.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.model.Retailer
import com.boutiquesworld.model.StoreCategory
import com.boutiquesworld.repository.ProductRepository
import com.boutiquesworld.repository.ProfileRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ViewModel for ProductsFragment.
 */
@Singleton
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val profileRepository: ProfileRepository
) :
    ViewModel() {
    private val retailer: MutableLiveData<Retailer> =
        profileRepository.getRetailerMutableLiveData()
    private val storeFabrics: MutableLiveData<ArrayList<StoreCategory>> = productRepository.getFabricsMutableLiveData()
    private val storeDressMaterials: MutableLiveData<ArrayList<StoreCategory>> = productRepository.getDressMaterialsMutableLiveData()
    private val storeClothes: MutableLiveData<ArrayList<StoreCategory>> = productRepository.getClothesMutableLiveData()
    private val storeJewellery: MutableLiveData<ArrayList<StoreCategory>> = productRepository.getJewelleryMutableLiveData()

    init {
        viewModelScope.launch {
            getStoreProduct("Fabric", forceRefresh = false)
            getStoreProduct("Dress Material", forceRefresh = false)
            getStoreProduct("Clothing", forceRefresh = false)
            getStoreProduct("Jewellery", forceRefresh = false)
            profileRepository.updateRetailer()
            retailer.value?.let {
                if (it.businessCategory == "B")
                    getProducts(forceRefresh = false)
                else if (it.businessCategory == "D")
                    getSketches(forceRefresh = false)
                getStore(forceRefresh = false)
            }
        }
    }

    private val products: MutableLiveData<ArrayList<BaseProduct>> =
        productRepository.getProductsLiveData()
    private val fabrics: MutableLiveData<ArrayList<BaseProduct>> =
        productRepository.getFabricsLiveData()
    private val sketches: MutableLiveData<ArrayList<BaseProduct>> =
        productRepository.getSketchesMutableLiveData()

    fun getProductsLiveData(): LiveData<ArrayList<BaseProduct>> = products
    fun getFabricsLiveData(): LiveData<ArrayList<BaseProduct>> = fabrics
    fun getSketchesLiveData(): LiveData<ArrayList<BaseProduct>> = sketches
    fun getStoreFabricsLiveData(): LiveData<ArrayList<StoreCategory>> = storeFabrics
    fun getStoreClothesLiveData(): LiveData<ArrayList<StoreCategory>> = storeClothes
    fun getStoreDressMaterialsLiveData(): LiveData<ArrayList<StoreCategory>> = storeDressMaterials
    fun getStoreJewelleryLiveData(): LiveData<ArrayList<StoreCategory>> = storeJewellery

    fun getProducts(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                productRepository.getProductsForBusiness(it.shopId, forceRefresh)
            }
        }
    }

    fun getStore(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                productRepository.getStore(forceRefresh)
            }
        }
    }

    fun getSketches(forceRefresh: Boolean) {
        viewModelScope.launch {
            retailer.value?.let {
                productRepository.getSketches(it.shopId, forceRefresh)
            }
        }
    }

    fun getStoreProduct(productCategory: String, forceRefresh: Boolean) {
        viewModelScope.launch {
            productRepository.getStoreProducts(productCategory, forceRefresh)
        }
    }

    fun updateQuantityOfStoreProduct(productId: Int, quantity: Int, productCategory: String) {
        viewModelScope.launch {
            productRepository.updateQuantityOfStoreProduct(productId, quantity, productCategory)
        }
    }
}

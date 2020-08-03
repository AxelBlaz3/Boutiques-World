package com.boutiquesworld.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.boutiquesworld.data.ProductDao
import com.boutiquesworld.model.Product
import com.boutiquesworld.network.BoutiqueService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class for maintaining the products data.
 */
@Singleton
class ProductRepository @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val productDao: ProductDao
) {
    private val products: MutableLiveData<ArrayList<Product>> = MutableLiveData()

    fun getProductsLiveData(): MutableLiveData<ArrayList<Product>> = products

    /**
     * Helper method for getting the products.
     * @param businessId: Unique businessId for getting products
     * @param forceRefresh: Force data to be retrieved from Network.
     */
    suspend fun getProductsForBusiness(businessId: Int, forceRefresh: Boolean) =
        withContext(Dispatchers.IO) {
            try {
                val cachedProducts = productDao.getAllProducts()
                cachedProducts?.apply {
                    if (isNotEmpty() && !forceRefresh) {
                        this@ProductRepository.products.postValue(this as ArrayList<Product>)
                        return@withContext
                    }
                }
                val response = boutiqueService.getProducts(businessId).execute()
                if (!response.isSuccessful) {
                    Log.d(
                        this@ProductRepository.javaClass.simpleName,
                        response.errorBody()?.string()!!
                    )
                    return@withContext
                }
                val products = response.body()
                // Have a local cache of products in BoutiqueDatabase
                if (products != null)
                    insertProducts(products)
                this@ProductRepository.products.postValue(products)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun insertProducts(products: ArrayList<Product>) = withContext(Dispatchers.IO) {
        productDao.insertAllProducts(products)
    }
}
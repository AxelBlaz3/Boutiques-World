package com.boutiquesworld.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.boutiquesworld.data.ProductDao
import com.boutiquesworld.model.BaseProduct
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
    private val products: MutableLiveData<ArrayList<BaseProduct>> = MutableLiveData()
    private val fabrics: MutableLiveData<ArrayList<BaseProduct>> = MutableLiveData()

    fun getProductsLiveData(): MutableLiveData<ArrayList<BaseProduct>> = products
    fun getFabricsLiveData(): MutableLiveData<ArrayList<BaseProduct>> = fabrics

    /**
     * Helper method for getting the products.
     * @param businessId: Unique businessId for getting products
     * @param forceRefresh: Force data to be retrieved from Network.
     */
    suspend fun getProductsForBusiness(businessId: Int, forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val cachedProducts = productDao.getAllProducts()
                cachedProducts?.apply {
                    if (isNotEmpty() && !forceRefresh) {
                        this@ProductRepository.products.postValue(this as ArrayList<BaseProduct>)
                        return@withContext true
                    }
                }
                val response = boutiqueService.getProducts(businessId).execute()

                val products = response.body()
                // Have a local cache of fabrics in BoutiqueDatabase
                if (products != null) {
                    insertProducts(products)
                    this@ProductRepository.products.postValue(products as ArrayList<BaseProduct>)
                    return@withContext true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    suspend fun getFabrics(forceRefresh: Boolean) = withContext(Dispatchers.IO) {
        try {
            val cachedFabrics = productDao.getAllFabrics()
            cachedFabrics?.apply {
                if (isNotEmpty() && !forceRefresh) {
                    this@ProductRepository.fabrics.postValue(this as ArrayList<BaseProduct>)
                    return@withContext
                }
            }
            val response = boutiqueService.getFabrics().execute()
            if (!response.isSuccessful) {
                Log.d(
                    this@ProductRepository.javaClass.simpleName,
                    response.errorBody()?.string()!!
                )
                return@withContext
            }
            val fabrics = response.body()
            // Have a local cache of products in BoutiqueDatabase
            if (fabrics != null)
                insertFabrics(fabrics)
            this@ProductRepository.fabrics.postValue(fabrics as ArrayList<BaseProduct>)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun insertProducts(products: ArrayList<BaseProduct.Product>) =
        withContext(Dispatchers.IO) {
            productDao.insertAllProducts(products)
        }

    private suspend fun insertFabrics(fabrics: ArrayList<BaseProduct.Fabric>) =
        withContext(Dispatchers.IO) {
            productDao.insertAllFabrics(fabrics)
        }
}
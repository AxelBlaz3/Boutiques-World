@file:Suppress("UNCHECKED_CAST")

package com.boutiquesworld.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.boutiquesworld.data.BoutiqueDatabase
import com.boutiquesworld.data.ProductDao
import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.model.StoreCategory
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
    private val boutiqueDatabase: BoutiqueDatabase,
    private val productDao: ProductDao
) {
    private val products: MutableLiveData<ArrayList<BaseProduct>> = MutableLiveData()
    private val fabrics: MutableLiveData<ArrayList<BaseProduct>> = MutableLiveData()
    private val sketches: MutableLiveData<ArrayList<BaseProduct>> = MutableLiveData(ArrayList())
    private val storeFabrics: MutableLiveData<ArrayList<StoreCategory>> = MutableLiveData(
        ArrayList()
    )
    private val storeDressMaterials: MutableLiveData<ArrayList<StoreCategory>> =
        MutableLiveData(ArrayList())
    private val storeClothes: MutableLiveData<ArrayList<StoreCategory>> =
        MutableLiveData(ArrayList())
    private val storeJewellery: MutableLiveData<ArrayList<StoreCategory>> =
        MutableLiveData(ArrayList())

    fun getSketchesMutableLiveData(): MutableLiveData<ArrayList<BaseProduct>> = sketches
    fun getProductsLiveData(): MutableLiveData<ArrayList<BaseProduct>> = products
    fun getFabricsLiveData(): MutableLiveData<ArrayList<BaseProduct>> = fabrics
    fun getClothesMutableLiveData(): MutableLiveData<ArrayList<StoreCategory>> = storeClothes
    fun getFabricsMutableLiveData(): MutableLiveData<ArrayList<StoreCategory>> = storeFabrics
    fun getDressMaterialsMutableLiveData(): MutableLiveData<ArrayList<StoreCategory>> =
        storeDressMaterials

    fun getJewelleryMutableLiveData(): MutableLiveData<ArrayList<StoreCategory>> =
        storeJewellery

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

    suspend fun getStore(forceRefresh: Boolean) = withContext(Dispatchers.IO) {
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

    suspend fun getSketches(businessId: Int, forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    boutiqueDatabase.sketchDao().getSketches()?.let {
                        if (it.isNotEmpty()) {
                            sketches.postValue(it as ArrayList<BaseProduct>)
                            return@withContext true
                        }
                    }
                }

                val response = boutiqueService.getSketches(businessId).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        boutiqueDatabase.sketchDao().apply {
                            truncateSketches()
                            insertSketches(it)
                        }
                        sketches.postValue(it as ArrayList<BaseProduct>)
                        return@withContext true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    suspend fun getStoreProducts(productCategory: String, forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    when (productCategory) {
                        "Clothing" -> boutiqueDatabase.storeCategoryDao().getClothes()?.let {
                            if (it.isNotEmpty()) {
                                storeClothes.postValue(it as ArrayList<StoreCategory>)
                                return@withContext true
                            }
                        }
                        "Dress Material" -> boutiqueDatabase.storeCategoryDao().getDressMaterials()
                            ?.let {
                                if (it.isNotEmpty()) {
                                    storeDressMaterials.postValue(it as ArrayList<StoreCategory>)
                                    return@withContext true
                                }
                            }
                        "Fabric" -> boutiqueDatabase.storeCategoryDao().getFabrics()?.let {
                            if (it.isNotEmpty()) {
                                storeFabrics.postValue(it as ArrayList<StoreCategory>)
                                return@withContext true
                            }
                        }
                        else -> boutiqueDatabase.storeCategoryDao().getJewellery()?.let {
                            if (it.isNotEmpty()) {
                                storeJewellery.postValue(it as ArrayList<StoreCategory>)
                                return@withContext true
                            }
                        }
                    }
                }

                val response = when (productCategory) {
                    "Clothing" -> boutiqueService.getStoreClothing().execute()
                    "Dress Material" -> boutiqueService.getStoreDressMaterials().execute()
                    "Fabric" -> boutiqueService.getStoreFabrics().execute()
                    "Jewellery" -> boutiqueService.getStoreJewellery().execute()
                    else -> throw IllegalArgumentException("Unknown category $productCategory")
                }
                if (response.isSuccessful) {
                    response.body()?.let {
                        boutiqueDatabase.storeCategoryDao().apply {
                            when (productCategory) {
                                "Clothing" -> {
                                    truncateClothes()
                                    insertClothes(it as ArrayList<StoreCategory.Cloth>)
                                    storeClothes.postValue(it as ArrayList<StoreCategory>)
                                }
                                "Dress Material" -> {
                                    truncateDressMaterials()
                                    insertDressMaterials(it as ArrayList<StoreCategory.DressMaterial>)
                                    storeDressMaterials.postValue(it as ArrayList<StoreCategory>)
                                }
                                "Fabric" -> {
                                    truncateFabrics()
                                    insertFabrics(it as ArrayList<StoreCategory.Fabric>)
                                    storeFabrics.postValue(it as ArrayList<StoreCategory>)
                                }
                                else -> {
                                    truncateJewellery()
                                    insertJewellery(it as ArrayList<StoreCategory.Jewellery>)
                                    storeJewellery.postValue(it as ArrayList<StoreCategory>)
                                }
                            }
                        }
                        return@withContext true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    suspend fun updateQuantityOfStoreProduct(productId: Int, quantity: Int, productCategory: String) = withContext(Dispatchers.IO) {
        try {
            val response = boutiqueService.updateQuantityOfStoreProduct(productId, quantity, productCategory).execute()
            if (response.isSuccessful)
                getStore(forceRefresh = true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun insertProducts(products: ArrayList<BaseProduct.Product>) =
        withContext(Dispatchers.IO) {
            productDao.insertAllProducts(products)
        }

    private suspend fun insertFabrics(stores: ArrayList<BaseProduct.Store>) =
        withContext(Dispatchers.IO) {
            productDao.insertAllFabrics(stores)
        }
}
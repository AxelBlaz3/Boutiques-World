package com.boutiquesworld.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.boutiquesworld.model.Product
import com.boutiquesworld.network.BoutiqueService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val boutiqueService: BoutiqueService) {
    private val products: MutableLiveData<ArrayList<Product>> = MutableLiveData()

    fun getProductsLiveData(): MutableLiveData<ArrayList<Product>> = products

    suspend fun getProductsForBusiness(businessId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = boutiqueService.getProducts(businessId).execute()
            if (!response.isSuccessful) {
                Log.d(this@ProductRepository.javaClass.simpleName, response.errorBody()?.string()!!)
                return@withContext
            }
            products.postValue(response.body())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
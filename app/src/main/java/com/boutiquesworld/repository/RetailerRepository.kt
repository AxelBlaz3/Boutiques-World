package com.boutiquesworld.repository

import com.boutiquesworld.data.RetailerDao
import com.boutiquesworld.model.Retailer
import com.boutiquesworld.network.BoutiqueService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetailerRepository @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val retailerDao: RetailerDao
) {

    suspend fun loginRetailer(mobile: String, password: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val response = boutiqueService.login(mobile, password).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.apply {
                        if (!error) {
                            insertRetailer(retailer)
                            return@withContext true
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
            return@withContext false
    }

    private suspend fun insertRetailer(retailer: Retailer) = withContext(Dispatchers.IO) {
        retailerDao.insertRetailer(retailer)
    }
}
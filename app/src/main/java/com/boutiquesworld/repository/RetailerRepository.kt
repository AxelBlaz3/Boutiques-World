package com.boutiquesworld.repository

import com.boutiquesworld.data.RetailerDao
import com.boutiquesworld.model.Retailer
import com.boutiquesworld.network.BoutiqueService
import com.boutiquesworld.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class for maintaining retailer's data.
 */
@Singleton
class RetailerRepository @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val retailerDao: RetailerDao,
    private val sessionManager: SessionManager
) {

    /**
     * Login the retailer.
     * @param mobile: Phone number of the user.
     * @param password: Secret key
     * @return true if login is successful, false otherwise.
     */
    suspend fun loginRetailer(mobile: String, password: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val response = boutiqueService.login(mobile, password).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.apply {
                        if (!error) {
                            insertRetailer(retailer)
                            sessionManager.saveSession(isLoggedIn = true)
                            return@withContext true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    suspend fun getRetailer(): List<Retailer> = withContext(Dispatchers.IO) {
        return@withContext retailerDao.getRetailer()
    }

    private suspend fun insertRetailer(retailer: Retailer) = withContext(Dispatchers.IO) {
        retailerDao.insertRetailer(retailer)
    }
}

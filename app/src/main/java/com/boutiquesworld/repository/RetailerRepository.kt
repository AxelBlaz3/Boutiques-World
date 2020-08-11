package com.boutiquesworld.repository

import androidx.lifecycle.MutableLiveData
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
    private val retailer: MutableLiveData<Retailer> = MutableLiveData()

    fun getRetailerMutableLiveData(): MutableLiveData<Retailer> = retailer

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
                            this@RetailerRepository.retailer.postValue(retailer)
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

    suspend fun updateRetailer() = withContext(Dispatchers.IO) {
        if (retailer.value == null)
            retailerDao.getRetailer()?.apply {
                retailer.postValue(get(0))
            }
    }

    private suspend fun insertRetailer(retailer: Retailer) = withContext(Dispatchers.IO) {
        retailerDao.insertRetailer(retailer)
    }
}

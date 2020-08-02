package com.boutiquesworld.repository

import com.boutiquesworld.data.RetailerDao
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

    suspend fun loginRetailer(mobile: String, password: String) = withContext(Dispatchers.IO) {
        boutiqueService.login(mobile, password)
    }
}
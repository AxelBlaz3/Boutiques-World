package com.boutiquesworld.repository

import androidx.lifecycle.MutableLiveData
import com.boutiquesworld.data.BoutiqueDatabase
import com.boutiquesworld.model.Order
import com.boutiquesworld.network.BoutiqueService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val boutiqueDatabase: BoutiqueDatabase
) {
    private val orders: MutableLiveData<ArrayList<Order>> = MutableLiveData(ArrayList())

    fun getOrdersMutableLiveData(): MutableLiveData<ArrayList<Order>> = orders

    suspend fun updateOrders(businessId: Int, forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    boutiqueDatabase.orderDao().getOrders()?.let {
                        if (it.isNotEmpty()) {
                            orders.postValue(it as ArrayList<Order>)
                            return@withContext true
                        }
                    }
                }

                val response = boutiqueService.getOrders(businessId).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        boutiqueDatabase.orderDao().apply {
                            truncateOrders()
                            insertOrders(it)
                        }
                        orders.postValue(it)
                        return@withContext true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }
}
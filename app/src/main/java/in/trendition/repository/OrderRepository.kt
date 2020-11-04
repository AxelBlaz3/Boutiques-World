package `in`.trendition.repository

import `in`.trendition.data.BoutiqueDatabase
import `in`.trendition.model.Order
import `in`.trendition.network.BoutiqueService
import androidx.lifecycle.MutableLiveData
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

    suspend fun updateOrders(forceRefresh: Boolean): Boolean =
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

                val response = boutiqueService.getOrders().execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        boutiqueDatabase.orderDao().run {
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
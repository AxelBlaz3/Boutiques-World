package com.boutiquesworld.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.boutiquesworld.data.BoutiqueDatabase
import com.boutiquesworld.model.*
import com.boutiquesworld.network.BoutiqueService
import com.boutiquesworld.util.SessionManager
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class for maintaining retailer's data.
 */
@Singleton
class ProfileRepository @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val boutiqueDatabase: BoutiqueDatabase,
    private val sessionManager: SessionManager
) {
    private val retailer: MutableLiveData<Retailer> = MutableLiveData()
    private val boutiqueRequests: MutableLiveData<ArrayList<BoutiqueRequest>> =
        MutableLiveData(ArrayList())
    private val subscriptionHistory: MutableLiveData<ArrayList<Subscription>> = MutableLiveData(
        ArrayList()
    )
    private val payments: MutableLiveData<ArrayList<Payment>> = MutableLiveData(ArrayList())
    private val razorPayOrderId: MutableLiveData<String?> = MutableLiveData(null)
    private val isSubscriptionSuccessful: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val subscriptionPlans: MutableLiveData<ArrayList<SubscriptionPlan>> = MutableLiveData(
        ArrayList()
    )
    private val isLogoutSuccessful: MutableLiveData<Boolean> = MutableLiveData()

    fun getRetailerMutableLiveData(): MutableLiveData<Retailer> = retailer
    fun getBoutiqueRequestsMutableLiveData(): MutableLiveData<ArrayList<BoutiqueRequest>> =
        boutiqueRequests

    fun getSubscriptionHistoryMutableLiveData(): MutableLiveData<ArrayList<Subscription>> =
        subscriptionHistory

    fun getSubscriptionPlansMutableLiveData(): MutableLiveData<ArrayList<SubscriptionPlan>> =
        subscriptionPlans

    fun getPaymentsMutableLiveData(): MutableLiveData<ArrayList<Payment>> = payments

    fun getRazorPayOrderId(): MutableLiveData<String?> = razorPayOrderId
    fun getIsSubscriptionSuccessful(): MutableLiveData<Boolean?> = isSubscriptionSuccessful
    fun getIsLogoutSuccessfulMutableLiveData(): MutableLiveData<Boolean> = isLogoutSuccessful

    /**
     * Login the retailer.
     * @param mobile: Phone number of the user.
     * @param password: Secret key
     * @return true if login is successful, false otherwise.
     */
    suspend fun loginRetailer(mobile: String, password: String, loginType: Char): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val response = boutiqueService.login(mobile, password, loginType).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.apply {
                        if (!error) {
                            insertRetailer(retailer)
                            sessionManager.saveSession(isLoggedIn = true)
                            this@ProfileRepository.retailer.postValue(retailer)
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
            boutiqueDatabase.retailerDao().getRetailer()?.let {
                if (it.isNotEmpty())
                    retailer.postValue(it[0])
            }
    }

    suspend fun refreshRetailer(shopId: Int) = withContext(Dispatchers.IO) {
        try {
            val response = boutiqueService.getRetailer(shopId).execute()
            if (response.isSuccessful) {
                val responseBody = response.body()
                responseBody?.apply {
                    retailer.value?.let {
                        boutiqueDatabase.retailerDao().deleteRetailer(it)
                    }
                    insertRetailer(this)
                    retailer.postValue(this)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext false
    }

    suspend fun updateBoutiqueRequests(forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    val cachedBoutiqueRequests =
                        boutiqueDatabase.boutiqueRequestDao().getBoutiqueRequests()
                    cachedBoutiqueRequests?.let {
                        if (it.isNotEmpty()) {
                            boutiqueRequests.postValue(it as ArrayList<BoutiqueRequest>)
                            return@withContext true
                        }
                    }
                }
                val response = boutiqueService.getBoutiqueRequests().execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        boutiqueDatabase.boutiqueRequestDao().apply {
                            truncateBoutiqueRequest()
                            insertBoutiqueRequest(it)
                            boutiqueRequests.postValue(it)
                            return@withContext true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    suspend fun getSubscriptionHistory(businessId: Int, forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    boutiqueDatabase.subscriptionDao().getSubscriptionHistory()?.let {
                        if (it.isNotEmpty()) {
                            subscriptionHistory.postValue(it as ArrayList<Subscription>)
                            return@withContext true
                        }
                    }
                }

                val response = boutiqueService.getSubscriptionHistory(businessId).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d(this@ProfileRepository.javaClass.simpleName, "$it")
                        boutiqueDatabase.subscriptionDao().apply {
                            truncateSubscription()
                            insertSubscriptionHistory(it)
                            subscriptionHistory.postValue(it)
                            return@withContext true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    suspend fun getSubscriptionPlans(forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    boutiqueDatabase.subscriptionDao().getSubscriptionPlans()?.let {
                        if (it.isNotEmpty()) {
                            subscriptionPlans.postValue(it as ArrayList<SubscriptionPlan>)
                            return@withContext true
                        }
                    }
                }

                val response = boutiqueService.getSubscriptionPlans().execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        boutiqueDatabase.subscriptionDao().apply {
                            truncateSubscriptionPlan()
                            insertSubscriptionPlans(it)
                            subscriptionPlans.postValue(it)
                            return@withContext true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    suspend fun getPayments(businessId: Int, forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    boutiqueDatabase.paymentDao().getPayments()?.let {
                        if (it.isNotEmpty()) {
                            payments.postValue(it as ArrayList<Payment>)
                            return@withContext true
                        }
                    }
                }

                val response = boutiqueService.getPayments(businessId).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        boutiqueDatabase.paymentDao().apply {
                            truncatePayments()
                            insertPayments(it)
                            payments.postValue(it)
                            return@withContext true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    @Suppress("UNCHECKED_CAST")
    suspend fun generateRazorPayOrderId(orderId: String, price: String) =
        withContext(Dispatchers.IO) {
            try {
                val response = boutiqueService.genRazorPayOrderId(orderId, price).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        razorPayOrderId.postValue((it as LinkedTreeMap<String, String>)["order_id"])
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun verifySignature(
        planId: Int,
        businessId: Int,
        businessName: String,
        uuid: String,
        amount: String,
        paidDate: String,
        endDate: String,
        razorPayOrderId: String,
        razorPayPaymentId: String,
        razorPaySignature: String,
        subscriptionId: String
    ) = withContext(Dispatchers.IO) {
        try {
            val response = boutiqueService.verifySubscription(
                planId,
                businessId.toString(),
                businessName,
                uuid,
                amount,
                paidDate,
                endDate,
                razorPayOrderId,
                razorPayPaymentId,
                razorPaySignature,
                subscriptionId
            ).execute()
            isSubscriptionSuccessful.postValue(response.code() == 204 || response.code() == 200)
            return@withContext
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isSubscriptionSuccessful.postValue(false)
    }

    private suspend fun insertRetailer(retailer: Retailer) = withContext(Dispatchers.IO) {
        boutiqueDatabase.retailerDao().insertRetailer(retailer)
    }

    suspend fun logoutRetailer(retailer: Retailer) = withContext(Dispatchers.IO) {
        boutiqueDatabase.run {
            addressDao().run {
                truncateAddressList()
                truncateOrderAddressList()
            }
            sketchDao().truncateSketches()
            productDao().run {
                truncateProducts()
                truncateStore()
            }
            cartDao().truncateCartTable()
            orderDao().run {
                truncateOrders()
                truncateOrderAddress()
            }
            paymentDao().truncatePayments()
            boutiqueRequestDao().truncateBoutiqueRequest()
            subscriptionDao().run {
                truncateSubscription()
                truncateSubscriptionPlan()
            }
            retailerDao().deleteRetailer(retailer)
            sessionManager.deleteSession()
            isLogoutSuccessful.postValue(true)
        }
    }
}

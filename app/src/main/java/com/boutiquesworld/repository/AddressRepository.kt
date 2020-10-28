package com.boutiquesworld.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.boutiquesworld.data.BoutiqueDatabase
import com.boutiquesworld.model.Address
import com.boutiquesworld.model.OrderAddress
import com.boutiquesworld.network.BoutiqueService
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepository @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val boutiqueDatabase: BoutiqueDatabase
) {
    private val addressList: MutableLiveData<ArrayList<Address>> = MutableLiveData(ArrayList())
    private val orderAddressList: MutableLiveData<ArrayList<OrderAddress>> = MutableLiveData(ArrayList())
    private val razorPayOrderId: MutableLiveData<String> = MutableLiveData()

    fun getRazorPayOrderIdMutableLiveData(): MutableLiveData<String> = razorPayOrderId
    fun getAddressMutableLiveData(): MutableLiveData<ArrayList<Address>> = addressList
    fun getOrderAddressMutableLiveData(): MutableLiveData<ArrayList<OrderAddress>> = orderAddressList

    suspend fun updateAddressList(userId: Int, forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    boutiqueDatabase.addressDao().getAddressList()?.let {
                        if (it.isNotEmpty()) {
                            addressList.postValue(it as ArrayList<Address>)
                            return@withContext true
                        }
                    }
                    boutiqueDatabase.addressDao().getOrderAddressList()?.let {
                        if (it.isNotEmpty()) {
                            orderAddressList.postValue(it as ArrayList<OrderAddress>)
                            return@withContext true
                        }
                    }
                }
                val response = boutiqueService.getAddress(userId).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        boutiqueDatabase.addressDao().apply {
                            truncateAddressList()
                            insertAddress(it)
                        }
                        addressList.postValue(it)
                        return@withContext true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    suspend fun updateOrderAddressList(userId: Int, forceRefresh: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!forceRefresh) {
                boutiqueDatabase.addressDao().getOrderAddressList()?.let {
                    if (it.isNotEmpty()) {
                        orderAddressList.postValue(it as ArrayList<OrderAddress>)
                        return@withContext true
                    }
                }
            }
            val orderAddressResponse = boutiqueService.getOrderAddress(userId).execute()
            if (orderAddressResponse.isSuccessful) {
                orderAddressResponse.body()?.let {
                    boutiqueDatabase.addressDao().apply {
                        truncateOrderAddressList()
                        insertOrderAddress(it)
                    }
                    orderAddressList.postValue(it)
                    return@withContext true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext false
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun genRazorPayOrderIdAndUpdateCartWithOrderId(
        userId: String,
        orderId: String,
        price: Int
    ) = withContext(Dispatchers.IO) {
        try {
            val response =
                boutiqueService.genRazorPayOrderId(orderId, price = price.toString()).execute()
            val orderIdResponse = boutiqueService.updateCartOrderId(orderId, userId).execute()
            if (response.isSuccessful && orderIdResponse.isSuccessful) {
                response.body()?.let {
                    razorPayOrderId.postValue((it as LinkedTreeMap<String, String>)["order_id"])
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun verifyAndCapturePayment(
        razorPayOrderId: String,
        paymentId: String,
        signature: String,
        orderId: String,
        userId: String,
        cartCount: Int,
        amount: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = boutiqueService.verifyAndCapturePayment(
                razorPayOrderId,
                paymentId,
                signature,
                orderId,
                userId,
                cartCount,
                amount
            ).execute()

            Log.d(this@AddressRepository.javaClass.simpleName, "${response.code()}")
            return@withContext response.code() == 204
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext false
    }
}
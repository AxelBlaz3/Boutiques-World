package com.boutiquesworld.network.service

import android.util.Log
import com.boutiquesworld.model.BoutiqueRequest
import com.boutiquesworld.model.Order
import com.boutiquesworld.ui.order.OrderViewModel
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseNotificationsService : FirebaseMessagingService() {
    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var orderViewModel: OrderViewModel

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            profileViewModel.getRetailer().value?.let { retailer ->
                val gson = Gson()
                try {
                    if (retailer.businessCategory == "Y" || retailer.businessCategory == "F" || retailer.businessCategory == "S") {
                        // OrderItem
                        val businessIds = JSONArray(remoteMessage.data["orders"])
                        for (index in 0 until businessIds.length())
                            if (retailer.shopId.toString() == JSONObject(businessIds[index].toString()).get(
                                    "business_id"
                                )
                            ) {
                                orderViewModel.updateOrders(forceRefresh = true)
                                break
                            }
                    } else if (retailer.businessCategory == "B") {
                        // Boutique requests
                        val boutiqueRequest = gson.fromJson<BoutiqueRequest>(
                            gson.toJson(remoteMessage.data),
                            BoutiqueRequest::class.java
                        )

                        boutiqueRequest?.let {
                            profileViewModel.updateBoutiqueRequests(forceRefresh = true)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
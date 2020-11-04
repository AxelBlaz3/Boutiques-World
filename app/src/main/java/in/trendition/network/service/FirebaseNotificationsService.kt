package `in`.trendition.network.service

import `in`.trendition.R
import `in`.trendition.model.BoutiqueRequest
import `in`.trendition.model.Order
import `in`.trendition.ui.MainActivityViewModel
import `in`.trendition.ui.address.AddressViewModel
import `in`.trendition.ui.order.OrderViewModel
import `in`.trendition.ui.profile.ProfileViewModel
import `in`.trendition.util.Constants.ORDERS_CHANNEL_ID
import `in`.trendition.util.Constants.ORDERS_NOTIFICATION_CHANNEL_NAME
import `in`.trendition.util.Constants.ORDERS_NOTIFICATION_ID
import `in`.trendition.util.Constants.REQUESTS_CHANNEL_ID
import `in`.trendition.util.Constants.REQUESTS_NOTIFICATION_CHANNEL_NAME
import `in`.trendition.util.Constants.REQUESTS_NOTIFICATION_ID
import `in`.trendition.util.NotificationUtil
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseNotificationsService : FirebaseMessagingService() {
    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var orderViewModel: OrderViewModel

    @Inject
    lateinit var addressViewModel: AddressViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            if (remoteMessage.data["updates"] != null) {
                remoteMessage.data["updates"]?.let { update ->
                    mainActivityViewModel.run {
                        downloadUrl = JSONObject(update).getString("url")
                        setIsUpdateAvailable(isUpdateAvailable = true)
                    }
                    return
                }
            }
            profileViewModel.getRetailer().value?.let { retailer ->
                val gson = Gson()
                try {
                    if (retailer.businessCategory == "S") {
                        // OrderItem
                        val ordersJson = remoteMessage.data["orders"]
                        val orders = gson.fromJson(ordersJson, Array<Order>::class.java)
                        for (order in orders) {
                            val isCurrentBusinessOrder = order.businessId == retailer.shopId
                            if (isCurrentBusinessOrder) {
                                NotificationUtil.makeNotification(
                                    ORDERS_NOTIFICATION_ID,
                                    ORDERS_NOTIFICATION_CHANNEL_NAME,
                                    ORDERS_CHANNEL_ID,
                                    order.productName,
                                    getString(R.string.notif_new_order_message),
                                    applicationContext
                                )
                                addressViewModel.updateOrderAddressList(
                                    retailer.shopId,
                                    forceRefresh = true
                                )
                                orderViewModel.updateOrders(forceRefresh = true)
                                break
                            }
                        }
                    } else if (retailer.businessCategory == "B") {
                        // Boutique requests
                        val boutiqueRequest: BoutiqueRequest? = gson.fromJson(
                            gson.toJson(remoteMessage.data),
                            BoutiqueRequest::class.java
                        )

                        boutiqueRequest?.let {
                            NotificationUtil.makeNotification(
                                REQUESTS_NOTIFICATION_ID,
                                REQUESTS_NOTIFICATION_CHANNEL_NAME,
                                REQUESTS_CHANNEL_ID,
                                boutiqueRequest.typeOfDress,
                                getString(R.string.notif_new_request_message),
                                applicationContext
                            )

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
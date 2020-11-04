package `in`.trendition.ui.order

import `in`.trendition.model.Order
import `in`.trendition.network.BoutiqueService
import `in`.trendition.repository.OrderRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderViewModel @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val orderRepository: OrderRepository
) :
    ViewModel() {
    private val orders: MutableLiveData<ArrayList<Order>> =
        orderRepository.getOrdersMutableLiveData()
    private val isOrderConfirmedOrDispatched: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val isTrackingIdEmpty: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val isServiceProviderEmpty: MutableLiveData<Boolean?> = MutableLiveData(null)
    private val isDeliveryDateEmpty: MutableLiveData<Boolean?> = MutableLiveData(null)

    fun getOrders(): LiveData<ArrayList<Order>> = orders
    fun getIsOrderConfirmedOrDispatched(): LiveData<Boolean?> = isOrderConfirmedOrDispatched
    fun getIsTrackingIdEmpty(): LiveData<Boolean?> = isTrackingIdEmpty
    fun getIsServiceProviderEmpty(): LiveData<Boolean?> = isServiceProviderEmpty
    fun getIsDeliveryDateEmpty(): LiveData<Boolean?> = isDeliveryDateEmpty

    fun resetIsTrackingIdEmpty() {
        isTrackingIdEmpty.value = null
    }

    fun resetIsServiceProviderEmpty() {
        isServiceProviderEmpty.value = null
    }

    fun resetIsDeliveryDateEmpty() {
        isDeliveryDateEmpty.value = null
    }

    fun resetIsOrderConfirmedOrDispatched() {
        isOrderConfirmedOrDispatched.value = null
    }

    fun updateOrders(forceRefresh: Boolean) {
        viewModelScope.launch {
            orderRepository.updateOrders(forceRefresh)
        }
    }

    fun confirmOrDispatchOrder(
        order: Order,
        trackingId: String,
        serviceProvider: String,
        deliveryDate: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<Void>
                if (order.orderStatus == 0) {
                    response = boutiqueService.confirmOrder(
                        order.id,
                        order.orderStatus + 1
                    ).execute()
                }
                else { // Order status is 1, so dispatch
                    isTrackingIdEmpty.postValue(trackingId.isEmpty())
                    isServiceProviderEmpty.postValue(serviceProvider.isEmpty())
                    isDeliveryDateEmpty.postValue(deliveryDate.isEmpty())
                    if (trackingId.isEmpty() || serviceProvider.isEmpty() || deliveryDate.isEmpty())
                        return@launch

                    response = boutiqueService.dispatchOrder(
                        order.id,
                        order.orderId!!.toString(),
                        order.productId,
                        order.userId,
                        order.userCategory!!,
                        serviceProvider,
                        trackingId,
                        deliveryDate,
                        details = ""
                    ).execute()
                }
                if (response.code() == 204) {
                    isOrderConfirmedOrDispatched.postValue(true)
                    updateOrders(forceRefresh = true)
                } else
                    isOrderConfirmedOrDispatched.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isOrderConfirmedOrDispatched.postValue(false)
        }
    }
}
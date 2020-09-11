package com.boutiquesworld.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.model.Order
import com.boutiquesworld.repository.OrderRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderViewModel @Inject constructor(private val orderRepository: OrderRepository) :
    ViewModel() {
    private val orders: MutableLiveData<ArrayList<Order>> =
        orderRepository.getOrdersMutableLiveData()

    fun getOrders(): LiveData<ArrayList<Order>> = orders

    fun updateOrders(businessId: Int, forceRefresh: Boolean) {
        viewModelScope.launch {
            orderRepository.updateOrders(businessId, forceRefresh)
        }
    }
}
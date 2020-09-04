package com.boutiquesworld.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boutiquesworld.model.Cart
import com.boutiquesworld.repository.CartRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ViewModel for managing cart.
 */
@Singleton
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {
    private val cartItems: MutableLiveData<ArrayList<Cart>> =
        cartRepository.getCartItemsMutableLiveData()
    private val areCartItemsLoaded: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isNewCartItemPosted: MutableLiveData<Boolean> = MutableLiveData()
    private val isCartCheckoutClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    var cartTotal: Int = 0
    var orderId: String = ""
    var finalCartToCheckout = ArrayList<Cart>()

    fun getCart(): LiveData<ArrayList<Cart>> = cartItems

    fun getIsNewCartItemPosted(): LiveData<Boolean?> = isNewCartItemPosted

    fun getIsCartCheckoutClicked(): LiveData<Boolean> = isCartCheckoutClicked

    fun setIsCartCheckoutClicked(newValue: Boolean) {
        isCartCheckoutClicked.value = newValue
    }

    fun resetIsNewCartItemPosted() {
        isNewCartItemPosted.value = null
    }

    fun getAreCartItemsLoaded(): LiveData<Boolean> = areCartItemsLoaded

    fun setAreCartItemsLoaded(newValue: Boolean) {
        areCartItemsLoaded.value = newValue
    }

    fun updateCart(userId: Int, userCategory: String, forceRefresh: Boolean) {
        viewModelScope.launch {
            areCartItemsLoaded.value =
                cartRepository.updateCart(userId, userCategory, forceRefresh = forceRefresh)
        }
    }

    fun postCartItem(userId: Int, userCategory: String, forceRefresh: Boolean, cart: List<Cart>) {
        viewModelScope.launch {
            isNewCartItemPosted.value =
                cartRepository.postCartItem(userId, userCategory, forceRefresh, cart)
        }
    }

    fun updateCartItem(newCartItem: Cart) {
        viewModelScope.launch {
            cartRepository.updateCartItem(newCartItem)
        }
    }

    fun deleteCartItem(cart: Cart) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(cart)
        }
    }
}
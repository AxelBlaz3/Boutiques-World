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

    fun getCart(): LiveData<ArrayList<Cart>> = cartItems

    fun getIsNewCartItemPosted(): LiveData<Boolean?> = isNewCartItemPosted

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

    fun postCartItem(cart: List<Cart>) {
        viewModelScope.launch {
            isNewCartItemPosted.value = cartRepository.postCartItem(cart)
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

    private fun getQuantity(cart: Cart, shouldAdd: Boolean): Int {
        return if (shouldAdd)
            cart.quantity + 1
        else
            cart.quantity - 1
    }

    private fun getPrice(quantity: Int, productPrice: String, shouldAdd: Boolean): String {
        return if (shouldAdd)
            String.format("%.2f", productPrice.toFloat() + (productPrice.toFloat() / quantity))
        else
            String.format("%.2f", productPrice.toFloat() - (productPrice.toFloat() / quantity))
    }
}
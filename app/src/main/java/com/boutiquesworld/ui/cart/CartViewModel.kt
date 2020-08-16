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
class CartViewModel @Inject constructor(private val cartRepository: CartRepository) : ViewModel() {
    private val cartItems: MutableLiveData<ArrayList<Cart>> =
        cartRepository.getCartItemsMutableLiveData()

    init {
        updateCart(2, "B", forceRefresh = false)
    }

    fun getCart(): LiveData<ArrayList<Cart>> = cartItems

    fun updateCart(userId: Int, userCategory: String, forceRefresh: Boolean) {
        viewModelScope.launch {
            cartRepository.updateCart(userId, userCategory, forceRefresh = forceRefresh)
        }
    }

    fun insertNewCartItem(cartItems: List<Cart>) {
        viewModelScope.launch {
            cartRepository.insertCartItem(cartItems)
        }
    }

    fun updateCartItem(cart: Cart, shouldAdd: Boolean) {
        viewModelScope.launch {
            val newCart = cart.copy(
                quantity = getQuantity(cart, shouldAdd),
                productPrice = getPrice(cart.quantity, cart.productPrice, shouldAdd)
            )
            cartRepository.updateCartItem(cart, newCart)
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

    private fun getPrice(quantity: Int, productPrice: Int, shouldAdd: Boolean): Int {
        return if (shouldAdd)
            productPrice + (productPrice / quantity)
        else
            productPrice - (productPrice / quantity)
    }
}
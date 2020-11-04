package `in`.trendition.ui.cart

import `in`.trendition.model.Cart
import `in`.trendition.repository.CartRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun updateCart(userId: Int, businessCategory: String, forceRefresh: Boolean) {
        viewModelScope.launch {
            areCartItemsLoaded.value =
                cartRepository.updateCart(userId, businessCategory, forceRefresh = forceRefresh)
        }
    }

    fun clearCartList() {
        cartItems.value?.clear()
    }

    fun postCartItem(
        userId: Int,
        businessCategory: String,
        forceRefresh: Boolean,
        cart: List<Cart>
    ) {
        viewModelScope.launch {
            isNewCartItemPosted.value =
                cartRepository.postCartItem(userId, businessCategory, forceRefresh, cart)
        }
    }

    fun updateCartItem(newCartItem: Cart) {
        viewModelScope.launch {
            cartRepository.updateCartItem(newCartItem)
        }
    }

    fun deleteCartItem(cart: Cart, position: Int) {
        viewModelScope.launch {
            cartRepository.deleteCartItem(cart, position)
        }
    }

    fun updateCartItemQuantity(id: Int, newQuantity: Int) {
        cartItems.value?.let {
            if (it.isNotEmpty()) {
                val cartItemIndex = it.indexOfFirst { cart -> cart.id == id }
                it[cartItemIndex] = it[cartItemIndex].copy(quantity = newQuantity)
                cartTotal = 0
                cartTotal = it.sumBy { cart -> cart.productPrice.toInt() }
                cartItems.value = it
            }
        }
    }
}
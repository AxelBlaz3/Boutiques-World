package com.boutiquesworld.repository

import androidx.lifecycle.MutableLiveData
import com.boutiquesworld.data.CartDao
import com.boutiquesworld.model.Cart
import com.boutiquesworld.network.BoutiqueService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing the data of cart items.
 */
@Singleton
class CartRepository @Inject constructor(
    private val boutiqueService: BoutiqueService,
    private val cartDao: CartDao
) {
    private val cartItems: MutableLiveData<ArrayList<Cart>> = MutableLiveData()

    fun getCartItemsMutableLiveData(): MutableLiveData<ArrayList<Cart>> = cartItems

    /**
     * Update the cart.
     * @param userId: Update cart of User having id - userId.
     * @param userCategory: Category of user (B - Boutiques, F - Fabrics, U - Users)
     * @param forceRefresh: Whether to force refresh cart
     */
    suspend fun updateCart(userId: Int, userCategory: String, forceRefresh: Boolean) =
        withContext(Dispatchers.IO) {
            try {
                val cachedCartItems = cartDao.getCartItems()
                cachedCartItems?.let {
                    if (it.isNotEmpty() && !forceRefresh) {
                        cartItems.postValue(it as ArrayList<Cart>)
                        return@withContext
                    }
                }
                val response = boutiqueService.getCart(userId, userCategory).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        insertCartItem(it)
                        cartItems.postValue(it as ArrayList<Cart>)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun insertCartItem(cartItems: List<Cart>) = withContext(Dispatchers.IO) {
        cartDao.insertCartItems(cartItems)
    }

    suspend fun updateCartItem(
        oldCartItem: Cart,
        newCartItem: Cart
    ) = withContext(Dispatchers.IO) {
        // Update local database with newCartItem
        cartDao.updateCartItems(newCartItem)

        // Use a tempCart for modifying the list
        val tempCart = ArrayList<Cart>()
        tempCart.addAll(cartItems.value!!)

        // Get the index of oldCartItem and assign value with newCartItem
        val index = tempCart.indexOf(oldCartItem)
        if (index >= 0) {
            tempCart[index] = newCartItem
            cartItems.postValue(tempCart)
        }
    }

    suspend fun deleteCartItem(cart: Cart) = withContext(Dispatchers.IO) {
        cartDao.removeItemFromCart(cart)
        cartItems.value?.remove(cart)
    }
}
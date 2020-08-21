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
    private val cartItems: MutableLiveData<ArrayList<Cart>> = MutableLiveData(ArrayList())

    fun getCartItemsMutableLiveData(): MutableLiveData<ArrayList<Cart>> = cartItems

    /**
     * Update the cart.
     * @param userId: Update cart of User having id - userId.
     * @param userCategory: Category of user (B - Boutiques, F - Fabrics, U - Users)
     * @param forceRefresh: Whether to force refresh cart
     */
    suspend fun updateCart(userId: Int, userCategory: String, forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val cachedCartItems = cartDao.getCartItems()
                cachedCartItems?.let {
                    if (it.isNotEmpty() && !forceRefresh) {
                        cartItems.postValue(it as ArrayList<Cart>)
                        return@withContext true
                    }
                }
                val response = boutiqueService.getCart(userId, userCategory).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isNotEmpty()) {
                            cartDao.truncateCartTable()
                            insertCartItem(it)
                        } else
                            cartDao.truncateCartTable() // If list is empty, truncate the cart table.

                        cartItems.postValue(it as ArrayList<Cart>)
                        return@withContext true
                    }
                }
            } catch (e: ClassCastException) {
                e.printStackTrace()
                return@withContext true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext false
        }

    suspend fun postCartItem(cart: List<Cart>): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = boutiqueService.insertCartItem(cart).execute()
            if (response.isSuccessful) {
                insertCartItem(cart)
                this@CartRepository.cartItems.value?.let {
                    it.addAll(cart)
                    this@CartRepository.cartItems.postValue(it)
                }
                return@withContext true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext false
    }

    private suspend fun insertCartItem(cartItems: List<Cart>) = withContext(Dispatchers.IO) {
        cartDao.insertCartItems(cartItems)
    }

    suspend fun updateCartItem(
        newCartItem: Cart
    ) = withContext(Dispatchers.IO) {
        // Update local database with newCartItem
        cartDao.updateCartItems(newCartItem)
    }

    suspend fun deleteCartItem(cart: Cart) =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    boutiqueService.deleteCartItem(cart.userId, cart.productId, cart.userCategory)
                        .execute()
                if (response.isSuccessful) {
                    cartDao.removeItemFromCart(cart.productId)
                    cartItems.value?.let {
                        val index =
                            cartItems.value!!.indexOfFirst { oldCartItem -> oldCartItem.productId == cart.productId }
                        if (index > -1)
                            it.removeAt(index)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}
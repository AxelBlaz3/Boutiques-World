package `in`.trendition.repository

import `in`.trendition.data.CartDao
import `in`.trendition.model.Cart
import `in`.trendition.network.BoutiqueService
import androidx.lifecycle.MutableLiveData
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
     * @param forceRefresh: Whether to force refresh cart
     */
    suspend fun updateCart(userId: Int, businessCategory: String, forceRefresh: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (!forceRefresh) {
                    val cachedCartItems = cartDao.getCartItems()
                    cachedCartItems?.let {
                        if (it.isNotEmpty()) {
                            cartItems.postValue(it as ArrayList<Cart>)
                            return@withContext true
                        }
                    }
                }
                val response = boutiqueService.getCart(userId, businessCategory).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isNotEmpty()) {
                            cartDao.truncateCartTable()
                            insertCartItem(it)
                        } else
                            cartDao.truncateCartTable() // If new list is empty, truncate the current cart table.
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

    suspend fun postCartItem(
        userId: Int,
        businessCategory: String,
        forceRefresh: Boolean,
        cart: List<Cart>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = boutiqueService.insertCartItem(cart).execute()
            if (response.isSuccessful) {
                insertCartItem(cart)
                updateCart(userId, businessCategory, forceRefresh)
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

    suspend fun deleteCartItem(cart: Cart, position: Int) =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    boutiqueService.deleteCartItem(
                        cart.userId,
                        cart.productId,
                        cart.userCategory!!,
                        cart.size
                    ).execute()
                if (response.isSuccessful) {
                    cartDao.removeItemFromCart(cart.id)
                    cartItems.value?.removeAt(position)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}
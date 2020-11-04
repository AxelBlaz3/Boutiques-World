package `in`.trendition.data

import `in`.trendition.model.Cart
import androidx.room.*

@Dao
interface CartDao {

    /**
     * Get all items in cart.
     */
    @Query("SELECT * FROM Cart")
    fun getCartItems(): List<Cart>?

    /**
     * Add new item to cart.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartItems(cartItems: List<Cart>)

    /**
     * Delete item from the cart.
     */
    @Query("DELETE FROM Cart WHERE id=:id")
    fun removeItemFromCart(id: Int): Int

    /**
     * Truncate cart table.
     */
    @Query("DELETE FROM Cart")
    fun truncateCartTable(): Int

    /**
     * Update item in cart.
     */
    @Update
    fun updateCartItems(cart: Cart)
}
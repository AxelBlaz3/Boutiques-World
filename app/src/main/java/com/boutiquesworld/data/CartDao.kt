package com.boutiquesworld.data

import androidx.room.*
import com.boutiquesworld.model.Cart

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
    @Insert
    fun insertCartItems(cartItems: List<Cart>)

    /**
     * Delete item from the cart.
     */
    @Delete
    fun removeItemFromCart(cart: Cart)

    /**
     * Update item in cart.
     */
    @Update
    fun updateCartItems(cart: Cart)
}
package com.boutiquesworld.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.boutiquesworld.model.Product

/**
 * Dao interface for managing the products maintained by the retailer.
 */
@Dao
interface ProductDao {

    /**
     * Get all the products maintained by the retailer.
     */
    @Query("SELECT * FROM Product")
    fun getAllProducts(): LiveData<List<Product>>

    /**
     * Insert a new product.
     */
    @Insert
    fun insertProduct(product: Product)

    /**
     * Delete a product.
     */
    @Delete
    fun deleteProduct(product: Product)
}
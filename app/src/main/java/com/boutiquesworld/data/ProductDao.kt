package com.boutiquesworld.data

import androidx.room.*
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
    fun getAllProducts(): List<Product>?

    /**
     * Insert a new product.
     */
    @Insert
    fun insertProduct(product: Product)

    /**
     * Inserts a list of products.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllProducts(products: List<Product>)

    /**
     * Delete a product.
     */
    @Delete
    fun deleteProduct(product: Product)
}
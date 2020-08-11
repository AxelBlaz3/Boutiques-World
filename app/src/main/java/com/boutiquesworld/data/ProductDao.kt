package com.boutiquesworld.data

import androidx.room.*
import com.boutiquesworld.model.BaseProduct

/**
 * Dao interface for managing the products maintained by the retailer.
 */
@Dao
interface ProductDao {

    /**
     * Get all the products maintained by the retailer.
     */
    @Query("SELECT * FROM Product")
    fun getAllProducts(): List<BaseProduct.Product>?

    /**
     * Get all fabrics.
     */
    @Query("SELECT * FROM Fabric")
    fun getAllFabrics(): List<BaseProduct.Fabric>?

    /**
     * Inserts a list of fabrics.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFabrics(fabrics: List<BaseProduct.Fabric>)

    /**
     * Insert a new product.
     */
    @Insert
    fun insertProduct(product: BaseProduct.Product)

    /**
     * Inserts a list of products.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllProducts(products: List<BaseProduct.Product>)

    /**
     * Delete a product.
     */
    @Delete
    fun deleteProduct(product: BaseProduct.Product)
}
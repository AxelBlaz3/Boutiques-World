package com.boutiquesworld.data

import androidx.room.*
import com.boutiquesworld.model.Retailer

/**
 * Dao interface for managing the Retailer table
 */
@Dao
interface RetailerDao {

    /**
     * Returns the current logged in Retailer object. We just need the first object
     * from the list. Size of the list is always one.
     */
    @Query("SELECT * FROM Retailer")
    fun getRetailer(): List<Retailer>?

    /**
     * Inserts the retailer if logged in.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRetailer(retailer: Retailer)

    /**
     * Delete the retailer from the table if logs out.
     */
    @Delete
    fun deleteRetailer(retailer: Retailer)
}
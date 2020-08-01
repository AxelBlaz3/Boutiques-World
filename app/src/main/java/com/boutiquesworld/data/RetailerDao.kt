package com.boutiquesworld.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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
    fun getRetailer(): LiveData<List<Retailer>>

    /**
     * Inserts the retailer if logged in.
     */
    @Insert
    fun insertRetailer(retailer: Retailer)

    /**
     * Delete the retailer from the table if logs out.
     */
    @Delete
    fun deleteRetailer(retailer: Retailer)
}
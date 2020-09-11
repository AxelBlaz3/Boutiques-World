package com.boutiquesworld.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boutiquesworld.model.Address

@Dao
interface AddressDao {

    /**
     * Saves address to BoutiqueDatabase
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddress(addressList: List<Address>)

    /**
     * Get address list from Database
     */
    @Query("SELECT * FROM Address")
    fun getAddressList(): List<Address>?

    /**
     * Truncate Address table
     */
    @Query("DELETE FROM Address")
    fun truncateAddressList(): Int
}
package com.boutiquesworld.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boutiquesworld.model.BoutiqueRequest

@Dao
interface BoutiqueRequestDao {
    /**
     * Get all requests.
     */
    @Query("SELECT * FROM BoutiqueRequest")
    fun getBoutiqueRequests(): List<BoutiqueRequest>?

    /**
     * Add new request.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBoutiqueRequest(boutiqueRequests: List<BoutiqueRequest>)

    /**
     * Truncate BoutiqueRequest table.
     */
    @Query("DELETE FROM BoutiqueRequest")
    fun truncateBoutiqueRequest(): Int
}
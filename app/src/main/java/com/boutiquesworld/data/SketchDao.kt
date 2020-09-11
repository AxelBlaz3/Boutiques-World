package com.boutiquesworld.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boutiquesworld.model.BaseProduct

/**
 * Dao interface for managing the products maintained by the retailer.
 */
@Dao
interface SketchDao {

    /**
     * Get all the sketches maintained by the retailer.
     */
    @Query("SELECT * FROM Sketch")
    fun getSketches(): List<BaseProduct.Sketch>?

    /**
     * Inserts a list of sketches.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSketches(sketches: List<BaseProduct.Sketch>)

    /**
     * Truncate sketches table.
     */
    @Query("DELETE FROM Sketch")
    fun truncateSketches(): Int
}
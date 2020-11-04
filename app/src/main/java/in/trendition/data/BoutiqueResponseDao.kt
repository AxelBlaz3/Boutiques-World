package `in`.trendition.data

import `in`.trendition.model.BoutiqueResponse
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BoutiqueResponseDao {
    /**
     * Get all responses.
     */
    @Query("SELECT * FROM BoutiqueResponse")
    fun getBoutiqueResponses(): List<BoutiqueResponse>?

    /**
     * Add responses.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBoutiqueResponse(boutiqueResponses: List<BoutiqueResponse>)

    /**
     * Truncate BoutiqueResponse table.
     */
    @Query("DELETE FROM BoutiqueResponse")
    fun truncateBoutiqueResponse(): Int
}
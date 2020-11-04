package `in`.trendition.data

import `in`.trendition.model.BoutiqueRequest
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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
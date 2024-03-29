package `in`.trendition.data

import `in`.trendition.model.Payment
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PaymentDao {

    /**
     * Get payments from Database.
     */
    @Query("SELECT * FROM Payment")
    fun getPayments(): List<Payment>?

    /**
     * Save payments to Database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPayments(payments: List<Payment>)

    /**
     * Truncate Payment table.
     */
    @Query("DELETE FROM Payment")
    fun truncatePayments(): Int
}
package `in`.trendition.data

import `in`.trendition.model.Order
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderDao {

    /**
     * Get orders from Database.
     */
    @Query("SELECT * FROM `Order`")
    fun getOrders(): List<Order>?

    /**
     * Save orders to Database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orders: List<Order>)

    /**
     * Truncate Order table.
     */
    @Query("DELETE FROM `Order`")
    fun truncateOrders(): Int

    /**
     * Truncate OrderAddress table.
     */
    @Query("DELETE FROM OrderAddress")
    fun truncateOrderAddress(): Int
}
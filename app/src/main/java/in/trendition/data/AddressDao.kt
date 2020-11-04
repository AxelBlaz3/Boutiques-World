package `in`.trendition.data

import `in`.trendition.model.Address
import `in`.trendition.model.OrderAddress
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AddressDao {

    /**
     * Saves address to BoutiqueDatabase
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddress(addressList: List<Address>)

    /**
     * Saves OrderAddress to BoutiqueDatabase
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderAddress(addressList: List<OrderAddress>)

    /**
     * Get address list from Database
     */
    @Query("SELECT * FROM Address")
    fun getAddressList(): List<Address>?

    /**
     * Get OrderAddress list from Database
     */
    @Query("SELECT * FROM Address")
    fun getOrderAddressList(): List<OrderAddress>?

    /**
     * Truncate Address table
     */
    @Query("DELETE FROM Address")
    fun truncateAddressList(): Int

    /**
     * Truncate OrderAddress table
     */
    @Query("DELETE FROM OrderAddress")
    fun truncateOrderAddressList(): Int
}
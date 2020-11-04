package `in`.trendition.data

import `in`.trendition.model.Subscription
import `in`.trendition.model.SubscriptionPlan
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SubscriptionDao {
    /**
     * Get subscriptions from Database.
     */
    @Query("SELECT * FROM Subscription")
    fun getSubscriptionHistory(): List<Subscription>?

    /**
     * Save subscriptions to Database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubscriptionHistory(subscriptions: List<Subscription>)

    /**
     * Truncate Subscription table.
     */
    @Query("DELETE FROM Subscription")
    fun truncateSubscription(): Int

    /**
     * Get subscription plans from Database.
     */
    @Query("SELECT * FROM SubscriptionPlan")
    fun getSubscriptionPlans(): List<SubscriptionPlan>?

    /**
     * Save subscription plans to Database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubscriptionPlans(subscriptionPlans: List<SubscriptionPlan>)

    /**
     * Truncate SubscriptionPlan table.
     */
    @Query("DELETE FROM SubscriptionPlan")
    fun truncateSubscriptionPlan(): Int
}
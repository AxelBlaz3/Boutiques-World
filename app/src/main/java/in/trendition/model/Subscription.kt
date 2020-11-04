package `in`.trendition.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Subscription(
    @SerializedName("id") @PrimaryKey val id: Int,
    @SerializedName("plan_id") val planId: Int,
    @SerializedName("business_id") val businessId: String,
    @SerializedName("business_name") val businessName: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("paid_date") val paidDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("razorpay_order_id") val razorPayOrderId: String,
    @SerializedName("razorpay_payment_id") val razorPayPaymentId: String,
    @SerializedName("subscription_id") val subscriptionId: String
)
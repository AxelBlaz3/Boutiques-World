package `in`.trendition.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Cart(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_description") val productDescription: String?,
    @SerializedName("product_category") val productCategory: String,
    @SerializedName("product_type") val productType: String,
    @SerializedName("product_image") val productImage: String,
    @SerializedName("size") var size: String = "",
    @SerializedName("minimum_quantity") var minimumQuantity: Int = 1,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("available_quantity") val availableQuantity: Int,
    @SerializedName("delivery_time") val deliveryTime: String,
    @SerializedName("product_price") var productPrice: String,
    @SerializedName("business_id") val businessId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("order_id") val orderId: String? = "-1",
    @SerializedName("business_category") val businessCategory: String,
    @SerializedName("user_category") val userCategory: String? = "R",
    @SerializedName("order_status") val orderStatus: Int = 0
)
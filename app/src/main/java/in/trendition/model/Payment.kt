package `in`.trendition.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Payment(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_description") val productDescription: String?,
    @SerializedName("product_category") val productCategory: String,
    @SerializedName("product_type") val productType: String,
    @SerializedName("product_image") val productImage: String,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("available_quantity") val availableQuantity: Int,
    @SerializedName("product_price") var productPrice: String,
    @SerializedName("business_id") val businessId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("order_id") val orderId: String? = "-1",
    @SerializedName("business_category") val businessCategory: String,
    @SerializedName("user_category") val userCategory: String? = "R",
    @SerializedName("order_status") val paymentStatus: Int = 0
) : Parcelable
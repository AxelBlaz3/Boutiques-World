package `in`.trendition.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class OrderAddress(
    @PrimaryKey(autoGenerate = true) @SerializedName("id") val id: Int,
    @SerializedName("fullname") val fullName: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("pincode") val pincode: String,
    @SerializedName("flat") val flat: String,
    @SerializedName("area") val area: String,
    @SerializedName("landmark") val landmark: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("order_id") val orderId: String,
    @SerializedName("address") var isChecked: Boolean = false,
    @SerializedName("user_category") val userCategory: String? = "R"
)
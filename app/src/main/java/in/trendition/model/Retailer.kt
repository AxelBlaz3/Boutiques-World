package `in`.trendition.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Retailer model. Also acts as an entity. Used for maintaining the
 * information of the currently logged in retailer.
 */
@Entity
data class Retailer(
    @SerializedName("shop_id") @PrimaryKey val shopId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("email") val email: String,
    @SerializedName("business_name") val businessName: String,
    @SerializedName("business_logo") val businessLogo: String,
    @SerializedName("designer_info") val designerInfo: String?,
    @SerializedName("business_address") val businessAddress: String,
    @SerializedName("timings") val timings: String? = "",
    @SerializedName("zone") val zone: String,
    @SerializedName("state") val state: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("views") val views: String = "0",
    @SerializedName("leads") val leads: String = "0",
    @SerializedName("likes") val likes: String = "0",
    @SerializedName("rating") val rating: String = "0",
    @SerializedName("business_category") val businessCategory: String,
    @SerializedName("business_category_type") val businessCategoryType: String,
    @SerializedName("specialization") val specialization: String = ""
)
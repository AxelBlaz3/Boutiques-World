package `in`.trendition.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user") val retailer: Retailer
)
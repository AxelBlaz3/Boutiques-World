package com.boutiquesworld.model

import com.google.gson.annotations.SerializedName

data class Retailer(
    @SerializedName("shop_id") val shopId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("user_image") val userImage: String,
    @SerializedName("business_name") val businessName: String,
    @SerializedName("business_logo") val businessLogo: String,
    @SerializedName("designer_info") val designerInfo: String,
    @SerializedName("business_address") val businessAddress: String,
    @SerializedName("business_map_location") val businessMapLocation: String,
    @SerializedName("zone") val zone: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("date") val date: String
)
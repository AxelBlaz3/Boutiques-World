package com.boutiquesworld.model

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
    @SerializedName("zone") val zone: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("business_category") val businessCategory: String
)
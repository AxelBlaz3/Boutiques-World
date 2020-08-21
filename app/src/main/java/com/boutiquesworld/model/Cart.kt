package com.boutiquesworld.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Cart(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_description") val productDescription: String?,
    @SerializedName("product_category") val productCategory: String = "Fabrics",
    @SerializedName("product_type") val productType: String,
    @SerializedName("product_image") val productImage: String,
    @SerializedName("quantity") var quantity: Int,
    @SerializedName("max_quantity") val maxQuantity: Int,
    @SerializedName("product_price") var productPrice: String,
    @SerializedName("business_id") val businessId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_category") val userCategory: String
)
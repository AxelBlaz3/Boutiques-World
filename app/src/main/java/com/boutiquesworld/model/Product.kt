package com.boutiquesworld.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("product_id") val productId: Int = 0,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_type") val productType: String,
    @SerializedName("product_description") val productDescription: String,
    @SerializedName("start_price") val startPrice: Int,
    @SerializedName("end_price") val endPrice: Int,
    @SerializedName("product_color") val productColor: String,
    @SerializedName("product_thumb") val productThumb: String,
    @SerializedName("product_image1") val productImage1: String,
    @SerializedName("product_image2") val productImage2: String,
    @SerializedName("product_image3") val productImage3: String,
    @SerializedName("business_id") val businessId: Int,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("business_name") val businessName: String,
    @SerializedName("zone") val zone: String,
    @SerializedName("upid") val upid: String,
    @SerializedName("likes") val likes: Int,
    @SerializedName("date") val date: String,
    @SerializedName("product_status") val productStatus: Int = 0
)
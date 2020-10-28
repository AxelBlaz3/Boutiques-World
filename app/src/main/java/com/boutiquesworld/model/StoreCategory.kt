package com.boutiquesworld.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

sealed class StoreCategory {

    @Entity
    @Parcelize
    data class Cloth(
        @SerializedName("product_id") @PrimaryKey val productId: Int,
        @SerializedName("product_name") val productName: String,
        @SerializedName("product_category") val productCategory: String,
        @SerializedName("product_type") val productType: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("product_description") val productDescription: String,
        @SerializedName("product_price") val productPrice: String,
        @SerializedName("product_colour") val productColor: String,
        @SerializedName("product_cloth") val productCloth: String,
        @SerializedName("product_fabric") val productFabric: String,
        @SerializedName("product_occasion") val productOccasion: String,
        @SerializedName("available_quantity") val availableQuantity: String,
        @SerializedName("delivery_time") val deliveryTime: String,
        @SerializedName("size_s") val sizeS: Int,
        @SerializedName("size_m") val sizeM: Int,
        @SerializedName("size_l") val sizeL: Int,
        @SerializedName("size_xl") val sizeXL: Int,
        @SerializedName("size_xxl") val sizeXXL: Int,
        @SerializedName("product_thumb") val productThumb: String,
        @SerializedName("product_image1") val productImage1: String,
        @SerializedName("product_image2") val productImage2: String,
        @SerializedName("product_image3") val productImage3: String,
        @SerializedName("product_image4") val productImage4: String,
        @SerializedName("product_image5") val productImage5: String,
        @SerializedName("business_category") val businessCategory: String,
        @SerializedName("business_category_type") val businessCategoryType: String,
        @SerializedName("business_id") val businessId: String,
        @SerializedName("uuid") val uuid: String,
        @SerializedName("business_name") val businessName: String,
        @SerializedName("upid") val upid: String,
        @SerializedName("likes") val likes: Int,
        @SerializedName("date") val date: String,
        @SerializedName("product_status") val productStatus: Int = 0
    ) : StoreCategory(), Parcelable

    @Parcelize
    @Entity
    data class Jewellery(
        @SerializedName("product_id") @PrimaryKey val productId: Int,
        @SerializedName("product_name") val productName: String,
        @SerializedName("product_category") val productCategory: String,
        @SerializedName("product_type") val productType: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("product_material") val productMaterial: String,
        @SerializedName("product_description") val productDescription: String,
        @SerializedName("product_price") val productPrice: String,
        @SerializedName("product_colour") val productColor: String,
        @SerializedName("available_quantity") val availableQuantity: String,
        @SerializedName("delivery_time") val deliveryTime: String,
        @SerializedName("product_thumb") val productThumb: String,
        @SerializedName("product_image1") val productImage1: String,
        @SerializedName("product_image2") val productImage2: String,
        @SerializedName("product_image3") val productImage3: String,
        @SerializedName("product_image4") val productImage4: String,
        @SerializedName("product_image5") val productImage5: String,
        @SerializedName("business_category") val businessCategory: String,
        @SerializedName("business_category_type") val businessCategoryType: String,
        @SerializedName("business_id") val businessId: String,
        @SerializedName("uuid") val uuid: String,
        @SerializedName("business_name") val businessName: String,
        @SerializedName("upid") val upid: String,
        @SerializedName("likes") val likes: Int,
        @SerializedName("date") val date: String,
        @SerializedName("product_status") val productStatus: Int = 0
    ) : StoreCategory(), Parcelable

    @Parcelize
    @Entity
    data class Fabric(
        @SerializedName("product_id") @PrimaryKey val productId: Int,
        @SerializedName("product_name") val productName: String,
        @SerializedName("product_category") val productCategory: String,
        @SerializedName("product_type") val productType: String,
        @SerializedName("product_description") val productDescription: String,
        @SerializedName("product_price") val productPrice: String,
        @SerializedName("product_colour") val productColor: String,
        @SerializedName("product_cloth") val productCloth: String,
        @SerializedName("product_fabric") val productFabric: String,
        @SerializedName("product_pattern") val productPattern: String,
        @SerializedName("width") val productWidth: String,
        @SerializedName("weight") val productWeight: String,
        @SerializedName("available_quantity") val availableQuantity: String,
        @SerializedName("minimum_quantity") val minimumQuantity: String,
        @SerializedName("delivery_time") val deliveryTime: String,
        @SerializedName("product_thumb") val productThumb: String,
        @SerializedName("product_image1") val productImage1: String,
        @SerializedName("product_image2") val productImage2: String,
        @SerializedName("product_image3") val productImage3: String,
        @SerializedName("product_image4") val productImage4: String,
        @SerializedName("product_image5") val productImage5: String,
        @SerializedName("business_category") val businessCategory: String,
        @SerializedName("business_category_type") val businessCategoryType: String,
        @SerializedName("business_id") val businessId: String,
        @SerializedName("uuid") val uuid: String,
        @SerializedName("business_name") val businessName: String,
        @SerializedName("upid") val upid: String,
        @SerializedName("likes") val likes: Int,
        @SerializedName("date") val date: String,
        @SerializedName("product_status") val productStatus: Int = 0
    ) : StoreCategory(), Parcelable

    @Parcelize
    @Entity
    data class DressMaterial(
        @SerializedName("product_id") @PrimaryKey val productId: Int,
        @SerializedName("product_name") val productName: String,
        @SerializedName("product_category") val productCategory: String,
        @SerializedName("product_type") val productType: String,
        @SerializedName("set_piece") val setPiece: String,
        @SerializedName("product_description") val productDescription: String,
        @SerializedName("product_price") val productPrice: String,
        @SerializedName("product_colour") val productColor: String,
        @SerializedName("product_cloth") val productCloth: String,
        @SerializedName("product_fabric") val productFabric: String,
        @SerializedName("product_pattern") val productPattern: String,
        @SerializedName("weight") val productWeight: String,
        @SerializedName("top_measurement") val topMeasurement: String,
        @SerializedName("bottom_measurement") val bottomMeasurement: String,
        @SerializedName("dupatta_measurement") val dupattaMeasurement: String,
        @SerializedName("available_quantity") val availableQuantity: String,
        @SerializedName("delivery_time") val deliveryTime: String,
        @SerializedName("product_thumb") val productThumb: String,
        @SerializedName("product_image1") val productImage1: String,
        @SerializedName("product_image2") val productImage2: String,
        @SerializedName("product_image3") val productImage3: String,
        @SerializedName("product_image4") val productImage4: String,
        @SerializedName("product_image5") val productImage5: String,
        @SerializedName("business_category") val businessCategory: String,
        @SerializedName("business_category_type") val businessCategoryType: String,
        @SerializedName("business_id") val businessId: String,
        @SerializedName("uuid") val uuid: String,
        @SerializedName("business_name") val businessName: String,
        @SerializedName("upid") val upid: String,
        @SerializedName("likes") val likes: Int,
        @SerializedName("date") val date: String,
        @SerializedName("product_status") val productStatus: Int = 0
    ) : StoreCategory(), Parcelable
}

fun StoreCategory.Cloth.toCart(quantity: Int, price: String, sizeChosen: String, retailer: Retailer) =
    Cart(
        id = -1,
        productId = productId,
        productName = productName,
        productDescription = productDescription,
        productCategory = productCategory,
        productType = productType,
        minimumQuantity = 1,
        size = sizeChosen,
        quantity = quantity,
        availableQuantity = availableQuantity.toInt(),
        deliveryTime = deliveryTime,
        productPrice = price,
        orderId = "",
        orderStatus = 0,
        userCategory = "R",
        businessCategory = retailer.businessCategory,
        businessId = businessId.toInt(),
        userId = retailer.shopId,
        productImage = productImage1
    )

fun StoreCategory.Fabric.toCart(quantity: Int, price: String, retailer: Retailer) =
    Cart(
        id = -1,
        productId = productId,
        productName = productName,
        productDescription = productDescription,
        productCategory = productCategory,
        productType = productType,
        size = "",
        minimumQuantity = minimumQuantity.toInt(),
        quantity = quantity,
        availableQuantity = availableQuantity.toInt(),
        deliveryTime = deliveryTime,
        productPrice = price,
        orderId = "",
        orderStatus = 0,
        userCategory = "R",
        businessCategory = retailer.businessCategory,
        businessId = businessId.toInt(),
        userId = retailer.shopId,
        productImage = productImage1
    )

fun StoreCategory.DressMaterial.toCart(
    quantity: Int,
    price: String,
    retailer: Retailer
) =
    Cart(
        id = -1,
        productId = productId,
        productName = productName,
        productDescription = productDescription,
        productCategory = productCategory,
        productType = productType,
        size = "",
        minimumQuantity = 1,
        quantity = quantity,
        availableQuantity = availableQuantity.toInt(),
        deliveryTime = deliveryTime,
        productPrice = price,
        orderId = "",
        orderStatus = 0,
        userCategory = "R",
        businessCategory = retailer.businessCategory,
        businessId = businessId.toInt(),
        userId = retailer.shopId,
        productImage = productImage1
    )

fun StoreCategory.Jewellery.toCart(quantity: Int, price: String, retailer: Retailer) =
    Cart(
        id = -1,
        productId = productId,
        productName = productName,
        productDescription = productDescription,
        productCategory = productCategory,
        productType = productType,
        size = "",
        minimumQuantity = 1,
        quantity = quantity,
        availableQuantity = availableQuantity.toInt(),
        deliveryTime = deliveryTime,
        productPrice = price,
        orderId = "",
        orderStatus = 0,
        userCategory = "R",
        businessCategory = retailer.businessCategory,
        businessId = businessId.toInt(),
        userId = retailer.shopId,
        productImage = productImage1
    )
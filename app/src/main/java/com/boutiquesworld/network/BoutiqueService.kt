package com.boutiquesworld.network

import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.model.Cart
import com.boutiquesworld.model.LoginResponse
import com.boutiquesworld.model.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Interface for managing the REST API.
 */
interface BoutiqueService {

    @GET("new/API/products.php")
    fun getProducts(@Query("bid") businessId: Int): Call<ArrayList<BaseProduct.Product>>

    @GET("/new/API/fabrics.php")
    fun getFabrics(): Call<ArrayList<BaseProduct.Fabric>>

    @FormUrlEncoded
    @POST("/new/API/update_fabric.php")
    fun updateFabric(
        @Field("product_id") productId: Int,
        @Field("available_meters") availableMeters: Int
    ): Call<Any>

    @FormUrlEncoded
    @POST("/new/API/cart.php")
    fun getCart(
        @Field("user_id") userId: Int,
        @Field("user_category") userCategory: String
    ): Call<List<Cart>>

    @POST("/new/API/cart_insert_update_item.php")
    fun insertCartItem(@Body cart: List<Cart>): Call<Any>

    @FormUrlEncoded
    @POST("/new/API/cart_delete_item.php")
    fun deleteCartItem(
        @Field("user_id") userId: Int,
        @Field("product_id") productId: Int,
        @Field("user_category") userCategory: String
    ): Call<Any>

    @Multipart
    @POST("new/API/upload_product.php")
    fun postProduct(
        @PartMap formDataMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageFiles: List<MultipartBody.Part>
    ): Call<ProductResponse>

    @Multipart
    @POST("new/API/upload_fabric.php")
    fun postFabric(
        @PartMap formDataMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageFiles: List<MultipartBody.Part>
    ): Call<ProductResponse>

    @FormUrlEncoded
    @POST("new/API/login.php")
    fun login(
        @Field("mobile") mobile: String,
        @Field("password") password: String
    ): Call<LoginResponse>
}
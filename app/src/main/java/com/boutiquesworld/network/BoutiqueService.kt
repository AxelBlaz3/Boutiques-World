package com.boutiquesworld.network

import com.boutiquesworld.model.*
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

    @GET("/new/API/orders.php")
    fun getOrders(@Query("user_id") userId: Int): Call<ArrayList<Any>>

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

    @Multipart
    @POST("new/API/post_address.php")
    fun postAddress(
        @PartMap addressMap: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<Any>

    @FormUrlEncoded
    @POST("new/API/get_address.php")
    fun getAddress(@Field("user_id") userId: Int): Call<ArrayList<Address>>

    @FormUrlEncoded
    @POST("new/API/gen_razorpay_order_id.php")
    fun genRazorPayOrderId(
        @Field("order_id") orderId: String,
        @Field("price") price: String
    ): Call<Any>

    @FormUrlEncoded
    @POST("new/API/razorpay_verify_signature.php")
    fun verifyAndCapturePayment(
        @Field("razorpay_order_id") razorPayOrderId: String,
        @Field("razorpay_payment_id") paymentId: String,
        @Field("razorpay_signature") signature: String,
        @Field("order_id") orderId: String,
        @Field("user_id") userId: String,
        @Field("cart_count") cartCount: Int,
        @Field("amount_total") amountTotal: String
    ): Call<Any>

    @FormUrlEncoded
    @POST("new/API/update_cart_order_id.php")
    fun updateCartOrderId(
        @Field("order_id") orderId: String,
        @Field("user_id") userId: String
    ): Call<Any>
}
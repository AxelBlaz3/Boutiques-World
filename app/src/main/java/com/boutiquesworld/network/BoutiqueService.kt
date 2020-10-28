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
    fun getFabrics(): Call<ArrayList<BaseProduct.Store>>

    /**
     * @param productCategory: Can be any of the following:
     * 1. Fabric
     * 2. Clothing
     * 3. Dress Material
     * 4. Jewellery
     */
    @GET("/new/API/get_store_products.php")
    fun getStoreFabrics(@Query("product_category") productCategory: String = "Fabric"): Call<ArrayList<StoreCategory.Fabric>>

    @GET("/new/API/get_store_products.php")
    fun getStoreClothing(@Query("product_category") productCategory: String = "Clothing"): Call<ArrayList<StoreCategory.Cloth>>

    @GET("/new/API/get_store_products.php")
    fun getStoreDressMaterials(@Query("product_category") productCategory: String = "Dress Material"): Call<ArrayList<StoreCategory.DressMaterial>>

    @GET("/new/API/get_store_products.php")
    fun getStoreJewellery(@Query("product_category") productCategory: String = "Jewellery"): Call<ArrayList<StoreCategory.Jewellery>>

    @GET("/new/API/get_orders.php")
    fun getOrders(): Call<ArrayList<Order>>

    @GET("/new/API/sketches.php")
    fun getSketches(@Query("bid") businessId: Int): Call<ArrayList<BaseProduct.Sketch>>

    @GET("/new/API/get_retailer.php")
    fun getRetailer(@Query("shop_id") shopId: Int): Call<Retailer>

    @GET("/new/API/boutique_requests.php")
    fun getBoutiqueRequests(): Call<ArrayList<BoutiqueRequest>>

    @GET("/new/API/get_subscriptions.php")
    fun getSubscriptionHistory(@Query("business_id") businessId: Int): Call<ArrayList<Subscription>>

    @GET("/new/API/get_subscription_plans.php")
    fun getSubscriptionPlans(): Call<ArrayList<SubscriptionPlan>>

    @GET("/new/API/get_payments.php")
    fun getPayments(@Query("business_id") businessId: Int): Call<ArrayList<Payment>>

    @POST("/new/API/boutique_response.php")
    @FormUrlEncoded
    fun postBoutiqueResponse(
        @Field("request_id") requestId: Int,
        @Field("preparation_time") preparationTime: String,
        @Field("price") price: String,
        @Field("business_id") businessId: Int,
        @Field("request_status") requestStatus: Int
    ): Call<Void>

    @FormUrlEncoded
    @POST("/new/API/update_store_product_quantity.php")
    fun updateQuantityOfStoreProduct(
        @Field("product_id") productId: Int,
        @Field("available_quantity") availableQuantity: Int,
        @Field("product_category") productCategory: String
    ): Call<Any>

    @FormUrlEncoded
    @POST("/new/API/update_order_status.php")
    fun confirmOrder(
        @Field("order_id") orderId: String,
        @Field("order_status") orderStatus: Int,
        @Field("product_id") productId: Int,
        @Field("size") size: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("/new/API/dispatch_order.php")
    fun dispatchOrder(
        @Field("order_id") orderId: String,
        @Field("product_id") productId: Int,
        @Field("user_id") userId: Int,
        @Field("user_category") userCategory: String,
        @Field("service_provider") serviceProvider: String,
        @Field("tracking_id") trackingId: String,
        @Field("delivery_date") deliveryDate: String,
        @Field("details") details: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("/new/API/get_cart.php")
    fun getCart(
        @Field("user_id") userId: Int,
        @Field("business_category") businessCategory: String
    ): Call<List<Cart>>

    @POST("/new/API/cart_insert_update_item.php")
    fun insertCartItem(@Body cart: List<Cart>): Call<Any>

    @FormUrlEncoded
    @POST("/new/API/cart_delete_item.php")
    fun deleteCartItem(
        @Field("user_id") userId: Int,
        @Field("product_id") productId: Int,
        @Field("user_category") userCategory: String,
        @Field("size") size: String
    ): Call<Any>

    @Multipart
    @POST("new/API/upload_product.php")
    fun postProduct(
        @PartMap formDataMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageFiles: List<MultipartBody.Part>
    ): Call<ProductResponse>

    @Multipart
    @POST("/new/API/post_sketch.php")
    fun postSketch(
        @PartMap formDataMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageFiles: List<MultipartBody.Part>
    ): Call<Void>

    @Multipart
    @POST("new/API/post_store_product.php")
    fun postStoreProduct(
        @PartMap formDataMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageFiles: List<MultipartBody.Part>
    ): Call<Void>

    @FormUrlEncoded
    @POST("new/API/login.php")
    fun login(
        @Field("mobile") mobile: String,
        @Field("password") password: String,
        @Field("login_type") loginType: Char
    ): Call<LoginResponse>

    @Multipart
    @POST("new/API/post_address.php")
    fun postAddress(
        @PartMap addressMap: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<Any>

    @POST("/new/API/post_order_address.php")
    fun postOrderAddress(@Body address: Address): Call<Any>

    @FormUrlEncoded
    @POST("new/API/get_address.php")
    fun getAddress(@Field("user_id") userId: Int): Call<ArrayList<Address>>

    @FormUrlEncoded
    @POST("new/API/get_order_address.php")
    fun getOrderAddress(@Field("user_id") userId: Int): Call<ArrayList<OrderAddress>>

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
    @POST("new/API/razorpay_verify_subscription_signature.php")
    fun verifySubscription(
        @Field("plan_id") planId: Int,
        @Field("business_id") businessId: String,
        @Field("business_name") businessName: String,
        @Field("uuid") uuid: String,
        @Field("amount") amount: String,
        @Field("paid_date") paidDate: String,
        @Field("end_date") endDate: String,
        @Field("razorpay_order_id") razorPayOrderId: String,
        @Field("razorpay_payment_id") paymentId: String,
        @Field("razorpay_signature") signature: String,
        @Field("subscription_id") subscriptionId: String
    ): Call<Void>

    @FormUrlEncoded
    @POST("new/API/update_cart_order_id.php")
    fun updateCartOrderId(
        @Field("order_id") orderId: String,
        @Field("user_id") userId: String
    ): Call<Any>

    @FormUrlEncoded
    @POST("new/API/subscribe_boutique.php")
    fun subscribeBoutique(
        @Field("plan_id") planId: Int,
        @Field("business_id") businessId: String,
        @Field("business_name") businessName: String,
        @Field("uuid") uuid: String,
        @Field("amount") amount: String,
        @Field("paid_date") paidDate: String,
        @Field("end_date") endDate: String,
        @Field("razorpay_order_id") razorPayOrderId: String,
        @Field("razorpay_payment_id") paymentId: String,
        @Field("subscription_id") subscriptionId: String
    ): Call<Void>
}
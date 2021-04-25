package `in`.trendition.network

import `in`.trendition.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Interface for managing the REST API.
 */
interface BoutiqueService {

    @GET("products.php")
    fun getProducts(@Query("bid") businessId: Int): Call<ArrayList<BaseProduct.Product>>

    @GET("fabrics.php")
    fun getFabrics(): Call<ArrayList<BaseProduct.Store>>

    @GET("check_update.php")
    fun checkUpdate(): Call<Any>

    @GET("get_design_dropdown_items.php")
    fun getDesignDropDownItems(): Call<List<String>>

    @GET("get_cloth_dropdown_items.php")
    fun getClothDropDownItems(): Call<List<String>>

    @GET("get_clothing_type_dropdown_items.php")
    fun getClothingTypeDropDownItems(): Call<Map<String, List<String>>>

    @GET("get_boutique_category_dropdown_items.php")
    fun getBoutiqueCategoryDropDownItems(): Call<List<String>>

    @GET("get_jewellery_type_dropdown_items.php")
    fun getJewelleryTypeDropDownItems(): Call<Map<String, List<String>>>

    /**
     * @param productCategory: Can be any of the following:
     * 1. Fabric
     * 2. Clothing
     * 3. Dress Material
     * 4. Jewellery
     */
    @GET("get_store_products.php")
    fun getStoreFabrics(@Query("product_category") productCategory: String = "Fabric"): Call<ArrayList<StoreCategory.Fabric>>

    @GET("get_store_products.php")
    fun getStoreClothing(@Query("product_category") productCategory: String = "Clothing"): Call<ArrayList<StoreCategory.Cloth>>

    @GET("get_store_products.php")
    fun getStoreDressMaterials(@Query("product_category") productCategory: String = "Dress Material"): Call<ArrayList<StoreCategory.DressMaterial>>

    @GET("get_store_products.php")
    fun getStoreJewellery(@Query("product_category") productCategory: String = "Jewellery"): Call<ArrayList<StoreCategory.Jewellery>>

    @GET("get_orders.php")
    fun getOrders(): Call<ArrayList<Order>>

    @GET("sketches.php")
    fun getSketches(@Query("bid") businessId: Int): Call<ArrayList<BaseProduct.Sketch>>

    @GET("get_retailer.php")
    fun getRetailer(@Query("shop_id") shopId: Int): Call<LoginResponse>

    @GET("get_boutique_requests.php")
    fun getBoutiqueRequests(@Query("business_id") businessId: Int): Call<ArrayList<BoutiqueRequest>>

    @GET("get_boutique_responses.php")
    fun getBoutiqueResponses(@Query("business_id") businessId: Int): Call<ArrayList<BoutiqueResponse>>

    @GET("get_subscriptions.php")
    fun getSubscriptionHistory(@Query("business_id") businessId: Int): Call<ArrayList<Subscription>>

    @GET("get_subscription_plans.php")
    fun getSubscriptionPlans(): Call<ArrayList<SubscriptionPlan>>

    @GET("get_payments.php")
    fun getPayments(@Query("business_id") businessId: Int): Call<ArrayList<Payment>>

    @POST("boutique_response.php")
    @FormUrlEncoded
    fun postBoutiqueResponse(
        @Field("request_id") requestId: Int,
        @Field("preparation_time") preparationTime: String,
        @Field("price") price: String,
        @Field("business_id") businessId: Int,
        @Field("request_status") requestStatus: Int
    ): Call<Void>

    @FormUrlEncoded
    @POST("update_store_product_quantity.php")
    fun updateQuantityOfStoreProduct(
        @Field("product_id") productId: Int,
        @Field("available_quantity") availableQuantity: Int,
        @Field("product_category") productCategory: String
    ): Call<Any>

    @FormUrlEncoded
    @POST("update_order_status.php")
    fun confirmOrder(
        @Field("id") Id: Int,
        @Field("order_status") orderStatus: Int
    ): Call<Void>

    @FormUrlEncoded
    @POST("dispatch_order.php")
    fun dispatchOrder(
        @Field("id") id: Int,
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
    @POST("get_cart.php")
    fun getCart(
        @Field("user_id") userId: Int,
        @Field("business_category") businessCategory: String
    ): Call<List<Cart>>

    @POST("cart_insert_update_item.php")
    fun insertCartItem(@Body cart: List<Cart>): Call<Any>

    @FormUrlEncoded
    @POST("cart_delete_item.php")
    fun deleteCartItem(
        @Field("user_id") userId: Int,
        @Field("product_id") productId: Int,
        @Field("user_category") userCategory: String,
        @Field("size") size: String
    ): Call<Any>

    @Multipart
    @POST("upload_product.php")
    fun postProduct(
        @PartMap formDataMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageFiles: List<MultipartBody.Part>
    ): Call<Void>

    @Multipart
    @POST("post_sketch.php")
    fun postSketch(
        @PartMap formDataMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageFiles: List<MultipartBody.Part>
    ): Call<Void>

    @Multipart
    @POST("post_store_product.php")
    fun postStoreProduct(
        @PartMap formDataMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageFiles: List<MultipartBody.Part>
    ): Call<Void>

    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("mobile") mobile: String,
        @Field("password") password: String,
        @Field("login_type") loginType: Char
    ): Call<LoginResponse>

    @Multipart
    @POST("post_address.php")
    fun postAddress(
        @PartMap addressMap: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<Any>

    @POST("post_order_address.php")
    fun postOrderAddress(@Body address: Address): Call<Any>

    @FormUrlEncoded
    @POST("get_address.php")
    fun getAddress(@Field("user_id") userId: Int): Call<ArrayList<Address>>

    @FormUrlEncoded
    @POST("get_order_address.php")
    fun getOrderAddress(@Field("user_id") userId: Int): Call<ArrayList<OrderAddress>>

    @FormUrlEncoded
    @POST("gen_razorpay_order_id.php")
    fun genRazorPayOrderId(
        @Field("order_id") orderId: String,
        @Field("price") price: String
    ): Call<Any>

    @FormUrlEncoded
    @POST("razorpay_verify_signature.php")
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
    @POST("razorpay_verify_subscription_signature.php")
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
    @POST("update_cart_order_id.php")
    fun updateCartOrderId(
        @Field("order_id") orderId: String,
        @Field("user_id") userId: String
    ): Call<Any>

    @FormUrlEncoded
    @POST("subscribe_boutique.php")
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
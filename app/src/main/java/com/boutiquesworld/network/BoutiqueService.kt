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
    @POST("/new/API/cart.php")
    fun getCart(
        @Field("user_id") userId: Int,
        @Field("user_category") userCategory: String
    ): Call<List<Cart>>

    @FormUrlEncoded
    @POST("/new/API/cart_insert_item.php")
    fun insertCartItem(@Body cart: Cart)

    @Multipart
    @POST("new/API/upload_product.php")
    fun postProduct(
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
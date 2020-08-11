package com.boutiquesworld.network

import com.boutiquesworld.model.LoginResponse
import com.boutiquesworld.model.Product
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
    fun getProducts(@Query("bid") businessId: Int): Call<ArrayList<Product>>

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
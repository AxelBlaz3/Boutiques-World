package com.boutiquesworld.network

import com.boutiquesworld.model.Product
import com.boutiquesworld.model.Retailer
import retrofit2.Call
import retrofit2.http.*

/**
 * Interface for managing the REST API.
 */
interface BoutiqueService {

    @GET("staging/API/products.php")
    fun getProducts(@Query("bid") businessId: Int): Call<ArrayList<Product>>

    @POST("staging/API/upload_product.php")
    fun postProduct(product: Product): Call<Product>

    @FormUrlEncoded
    @POST("staging/API/login.php")
    fun login(@Field("mobile") mobile: String, @Field("password") password: String): Call<Retailer>
}
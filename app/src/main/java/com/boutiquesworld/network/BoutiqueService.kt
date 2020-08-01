package com.boutiquesworld.network

import com.boutiquesworld.model.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val API_ROUTE_STAGING = "staging/API/"
private const val API_ROUTE_MAIN = "API/"

interface BoutiqueService {

    @GET("${API_ROUTE_STAGING}products.php")
    fun getProducts(@Query("bid") businessId: Int): Call<ArrayList<Product>>

    @POST("${API_ROUTE_STAGING}UploadProduct")
    fun postProduct(product: Product): Call<Product>
}
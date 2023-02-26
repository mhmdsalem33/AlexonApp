package com.mhmd.alexon.data.newtwork

import com.mhmd.alexon.domain.models.*
import retrofit2.Response
import retrofit2.http.*

interface DummyApi {

    @POST("auth/login")
    suspend fun login(@Body userModel: UserModel): Response<LoginResponse>

    @GET("products/search")
    suspend fun searchToGetProducts(@Query("q") searchProduct: String): Response<SearchResponse>

    @GET("carts/{cartNumber}")
    suspend fun getCartProducts(@Path("cartNumber") cartNumber: Int): Response<CartResponse>

    @PUT("carts/{cartNumber}")
    suspend fun addProductToCart(
        @Path("cartNumber") cartNumber: Int,
        @Body product: AddToCartList
    ): Response<AddToCartResponse>
}
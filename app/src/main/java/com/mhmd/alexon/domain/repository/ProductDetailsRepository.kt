package com.mhmd.alexon.domain.repository

import com.mhmd.alexon.data.newtwork.DummyApi
import com.mhmd.alexon.domain.models.AddToCartList
import com.mhmd.alexon.domain.models.AddToCartProduct
import javax.inject.Inject

class ProductDetailsRepository @Inject constructor(private val dummyApi: DummyApi){
    suspend fun addProductToMyCart(myCartNumber : Int , product: AddToCartList) = dummyApi.addProductToCart(myCartNumber , product)
}
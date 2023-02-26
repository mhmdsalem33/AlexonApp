package com.mhmd.alexon.domain.repository

import com.mhmd.alexon.data.newtwork.DummyApi
import javax.inject.Inject

class CartRepository @Inject constructor(private val dummyApi: DummyApi ) {
    suspend fun getCartProducts(cartNumber : Int ) = dummyApi.getCartProducts(cartNumber)
}
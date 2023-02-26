package com.mhmd.alexon.domain.repository

import com.mhmd.alexon.data.newtwork.DummyApi
import javax.inject.Inject

class SearchRepository @Inject constructor( private val dummyApi: DummyApi ) {
    suspend fun searchToGetProducts(searchProduct : String) = dummyApi.searchToGetProducts(searchProduct)
}
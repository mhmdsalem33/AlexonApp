package com.mhmd.alexon.domain.models

data class SearchResponse(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)
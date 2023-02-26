package com.mhmd.alexon.domain.models

data class AddToCartResponse(
    val discountedTotal: Int,
    val id: String,
    val products: List<AddToCartProducts>,
    val total: Int,
    val totalProducts: Int,
    val totalQuantity: Int,
    val userId: Int
)
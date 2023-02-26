package com.mhmd.alexon.domain.models

data class CartResponse(
    val discountedTotal: Int,
    val id: Int,
    val products: List<CartProducts>,
    val total: Int,
    val totalProducts: Int,
    val totalQuantity: Int,
    val userId: Int
)
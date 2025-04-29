package com.example.ecommerceapp.domain.models

data class ProductDataModels(
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val finalPrice: String = "",
    val category: String = "",
    val image: String = "",
    val date: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val avilableUnits: Int = 0,
    var productId: String = "",
    val likes: Map<String, Int>? = null // New property for tracking likes
) {
    constructor() : this("", "", "", "", "", "", 0L, "", 0, "", null)
}
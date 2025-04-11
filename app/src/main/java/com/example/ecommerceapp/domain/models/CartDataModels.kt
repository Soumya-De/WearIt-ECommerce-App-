package com.example.ecommerceapp.domain.models

data class CartDataModels(
    val productId: String = "",
    val name: String = "",
    val image: String = "",
    val price: String = "",
    val quantity: String = "",
    val cartId: String = "", // <- Firestore document ID for cart item
    val size: String = "",
    val description: String = "",
    val category: String = ""
)
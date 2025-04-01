package com.example.ecommerceapp.domain.models

data class UserAddress(
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val pinCode: String = "",
    val phoneNumber: String = "",
)

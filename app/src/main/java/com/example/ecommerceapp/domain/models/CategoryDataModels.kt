package com.example.ecommerceapp.domain.models

data class CategoryDataModels(
    var name: String = "",
    var date: Long = System.currentTimeMillis(),
    var categoryImage: String = "",
    var createdBy: String = "",
)

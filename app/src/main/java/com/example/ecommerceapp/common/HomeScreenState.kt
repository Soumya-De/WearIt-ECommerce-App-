package com.example.ecommerceapp.common

import com.example.ecommerceapp.domain.models.BannerDataModels
import com.example.ecommerceapp.domain.models.CategoryDataModels
import com.example.ecommerceapp.domain.models.ProductDataModels

data class HomeScreenState(
    val isLoading: Boolean = true,
    val errorMessages: String? = null,
    val categories: List<CategoryDataModels>? = null,
    val products: List<ProductDataModels>? = null,
    val banners: List<BannerDataModels>? = null,
)
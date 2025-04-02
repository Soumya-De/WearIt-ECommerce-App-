package com.example.ecommerceapp.domain.useCase

import com.example.ecommerceapp.domain.repo.Repo
import javax.inject.Inject
import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.domain.models.CartDataModels
import kotlinx.coroutines.flow.Flow

class AddToCartUseCase @Inject constructor(private val repo: Repo) {
    fun addToCart(cartDataModels: CartDataModels): Flow<ResultState<String>> {
        return repo.addToCart(cartDataModels)
    }
}
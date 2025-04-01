package com.example.ecommerceapp.domain.useCase

import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.domain.models.CartDataModels
import com.example.ecommerceapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartUseCase @Inject constructor(private val repo: Repo) {
    fun getCart(): Flow<ResultState<List<CartDataModels>>> {
        return repo.getCart()
    }
}
package com.example.ecommerceapp.domain.useCase

import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.domain.models.ProductDataModels
import com.example.ecommerceapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSpecificCategoryItems @Inject constructor(private val repo: Repo) {
    fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductDataModels>>> {
        return repo.getSpecificCategoryItems(categoryName)

    }
}
package com.example.ecommerceapp.domain.useCase

import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.domain.models.CategoryDataModels
import com.example.ecommerceapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryInLimit @Inject constructor(private val repo: Repo) {
    fun getCategoryInLimit(): Flow<ResultState<List<CategoryDataModels>>> {
        return repo.getCategoriesInLimited()

    }
}
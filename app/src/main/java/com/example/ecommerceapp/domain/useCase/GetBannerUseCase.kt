package com.example.ecommerceapp.domain.useCase
import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.domain.models.BannerDataModels
import com.example.ecommerceapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBannerUseCase @Inject constructor(private val repo: Repo) {
    fun getBannerUseCase(): Flow<ResultState<List<BannerDataModels>>> {
        return repo.getBanner()

    }
}
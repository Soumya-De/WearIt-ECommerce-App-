package com.example.ecommerceapp.domain.useCase
import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.domain.models.UserDataParent
import com.example.ecommerceapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val repo: Repo) {
    fun getUserById(uid: String): Flow<ResultState<UserDataParent>> {
        return repo.getUserById(uid)

    }
}
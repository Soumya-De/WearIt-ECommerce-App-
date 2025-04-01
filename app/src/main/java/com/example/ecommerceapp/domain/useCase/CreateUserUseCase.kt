package com.example.ecommerceapp.domain.useCase

import com.example.ecommerceapp.domain.models.UserData
import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(private val repo: Repo) {
    fun createUserUseCase(userData: UserData): Flow<ResultState<String>> {
        return repo.RegisterUserWithEmailAndPassword(userData)
    }

}
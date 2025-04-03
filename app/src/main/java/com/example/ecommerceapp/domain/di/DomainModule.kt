package com.example.ecommerceapp.domain.di

import com.example.ecommerceapp.domain.repo.Repo
import com.example.ecommerceapp.domain.useCase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)  // Provides dependencies for the entire app
object DomainModule {

    @Provides
    fun provideGetAllProductUseCase(repo: Repo): GetAllProductUseCase {
        return GetAllProductUseCase(repo)
    }

    @Provides
    fun provideGetUserUseCase(repo: Repo): GetUserUseCase {
        return GetUserUseCase(repo)
    }

    @Provides
    fun provideGetCategoryInLimitUseCase(repo: Repo): GetCategoryInLimit {
        return GetCategoryInLimit(repo)
    }

    @Provides
    fun provideGetAllCategoryUseCase(repo: Repo): GetAllCategoryUseCase {
        return GetAllCategoryUseCase(repo)
    }

    @Provides
    fun provideGetProductById(repo: Repo): GetProductById {
        return GetProductById(repo)
    }

    @Provides
    fun provideGetCheckOutUseCase(repo: Repo): GetCheckOutUseCase {
        return GetCheckOutUseCase(repo)
    }

    @Provides
    fun provideGetSpecificCategoryItems(repo: Repo): GetSpecificCategoryItems {
        return GetSpecificCategoryItems(repo)
    }

    @Provides
    fun provideGetAllSuggestedProductUseCase(repo: Repo): GetAllSuggestedProductUseCase {
        return GetAllSuggestedProductUseCase(repo)
    }

    @Provides
    fun provideGetBannerUseCase(repo: Repo): GetBannerUseCase {
        return GetBannerUseCase(repo)
    }

    @Provides
    fun provideLoginUserUseCase(repo: Repo): LoginUserUseCase {
        return LoginUserUseCase(repo)
    }

    @Provides
    fun provideCreateUserUseCase(repo: Repo): CreateUserUseCase {
        return CreateUserUseCase(repo)
    }

    @Provides
    fun provideUpdateUserDataUseCase(repo: Repo): UpdateUserDataUseCase {
        return UpdateUserDataUseCase(repo)
    }

    @Provides
    fun provideUserProfileImageUseCase(repo: Repo): UserProfileImageUseCase {
        return UserProfileImageUseCase(repo)
    }

    @Provides
    fun provideAddToCartUseCase(repo: Repo): AddToCartUseCase {
        return AddToCartUseCase(repo)
    }

    @Provides
    fun provideGetAllFavUseCase(repo: Repo): GetAllFavUseCase {
        return GetAllFavUseCase(repo)
    }

    @Provides
    fun provideGetCartUseCase(repo: Repo): GetCartUseCase {
        return GetCartUseCase(repo)
    }

    @Provides
    fun provideGetProductsInLimitedUseCase(repo: Repo): GetProductsInLimitedUseCase {
        return GetProductsInLimitedUseCase(repo)
    }

    @Provides
    fun provideAddToFavUseCase(repo: Repo): AddToFavUseCase {
        return AddToFavUseCase(repo)
    }
}

package com.example.ecommerceapp.data.di

import com.example.ecommerceapp.data.repo.RepoImpl
import com.example.ecommerceapp.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideRepo(
        firebaseAuth: FirebaseAuth,  // Inject FirebaseAuth
        firebaseFirestore: FirebaseFirestore  // Inject FirebaseFirestore
    ): Repo {
        return RepoImpl(firebaseAuth, firebaseFirestore)
    }
}

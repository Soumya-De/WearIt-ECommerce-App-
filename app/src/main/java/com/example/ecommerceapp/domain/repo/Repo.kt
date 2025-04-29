package com.example.ecommerceapp.domain.repo

import android.net.Uri
import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.domain.models.BannerDataModels
import com.example.ecommerceapp.domain.models.CartDataModels
import com.example.ecommerceapp.domain.models.CategoryDataModels
import com.example.ecommerceapp.domain.models.ProductDataModels
import com.example.ecommerceapp.domain.models.UserData
import com.example.ecommerceapp.domain.models.UserDataParent
import kotlinx.coroutines.flow.Flow

interface Repo {
    suspend fun updateCartItemQuantity(productId: String, newQty: Int)
    suspend fun removeCartItem(productId: String)
    fun RegisterUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>>
    fun LoginUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>>
    fun getUserById(uid: String): Flow<ResultState<UserDataParent>>
    fun updateUserData(userDataParent: UserDataParent): Flow<ResultState<String>>
    fun userProfileImage(uri: Uri): Flow<ResultState<String>>
    fun getCategoriesInLimited(): Flow<ResultState<List<CategoryDataModels>>>
    fun getProductsInLimited(): Flow<ResultState<List<ProductDataModels>>>
    fun getAllProducts(): Flow<ResultState<List<ProductDataModels>>>
    fun getProductById(productId: String): Flow<ResultState<ProductDataModels>>
    fun addToCart(cartDataModels: CartDataModels): Flow<ResultState<String>>
    fun addTOFav(productDataModels: ProductDataModels): Flow<ResultState<String>>
    fun removeFromFav(productId: String): Flow<ResultState<String>>
    fun getAllFav(): Flow<ResultState<List<ProductDataModels>>>
    fun getCart(): Flow<ResultState<List<CartDataModels>>>
    fun getAllCategories(): Flow<ResultState<List<CategoryDataModels>>>
    fun getCheckout(productId: String): Flow<ResultState<ProductDataModels>>
    fun getBanner(): Flow<ResultState<List<BannerDataModels>>>
    fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductDataModels>>>
    fun getAllSuggestedProducts(): Flow<ResultState<List<ProductDataModels>>>
    fun getWishlistForUser(userId: String): Flow<ResultState<List<ProductDataModels>>>
    fun likeProduct(userId: String, productId: String): Flow<ResultState<Unit>>
}


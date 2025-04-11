package com.example.ecommerceapp.presentation.viewModels

import android.net.Uri

import com.example.ecommerceapp.common.HomeScreenState
import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.domain.models.UserDataParent
import com.example.ecommerceapp.domain.models.ProductDataModels
import com.example.ecommerceapp.domain.models.CartDataModels
import com.example.ecommerceapp.domain.models.UserData
import com.example.ecommerceapp.domain.models.CategoryDataModels
import com.example.ecommerceapp.domain.useCase.CreateUserUseCase
import com.example.ecommerceapp.domain.useCase.GetProductsInLimitedUseCase
import com.example.ecommerceapp.domain.useCase.GetAllFavUseCase
import com.example.ecommerceapp.domain.useCase.GetAllProductUseCase
import com.example.ecommerceapp.domain.useCase.GetCartUseCase
import com.example.ecommerceapp.domain.useCase.GetCategoryInLimit
import com.example.ecommerceapp.domain.useCase.GetAllCategoryUseCase
import com.example.ecommerceapp.domain.useCase.GetAllSuggestedProductUseCase
import com.example.ecommerceapp.domain.useCase.GetProductById
import com.example.ecommerceapp.domain.useCase.GetSpecificCategoryItems
import com.example.ecommerceapp.domain.useCase.AddToCartUseCase
import com.example.ecommerceapp.domain.useCase.GetUserUseCase
import com.example.ecommerceapp.domain.useCase.GetCheckOutUseCase
import com.example.ecommerceapp.domain.useCase.GetBannerUseCase
import com.example.ecommerceapp.domain.useCase.AddToFavUseCase
import com.example.ecommerceapp.domain.useCase.LoginUserUseCase
import com.example.ecommerceapp.domain.useCase.UpdateUserDataUseCase
import com.example.ecommerceapp.domain.useCase.UserProfileImageUseCase
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.repo.Repo
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class ECommerceAppViewModel @Inject constructor(
    private val getAllProductUseCase: GetAllProductUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getCategoryInLimitUseCase: GetCategoryInLimit,
    private val getAllCategoryUseCase: GetAllCategoryUseCase,
    private val getProductById: GetProductById,
    private val getCheckOutUseCase: GetCheckOutUseCase,
    private val getSpecificCategoryItems: GetSpecificCategoryItems,
    private val getAllSuggestedProductUseCase: GetAllSuggestedProductUseCase,
    private val getBannerUseCase: GetBannerUseCase,
    private val loginUseCase: LoginUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val userProfileImageUseCase: UserProfileImageUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val getAllFavUseCase: GetAllFavUseCase,
    private val getCartUseCase: GetCartUseCase,
    private val getProductsInLimitedUseCase: GetProductsInLimitedUseCase,
    private val addToFavUseCase: AddToFavUseCase,
    private val repo: Repo
) : ViewModel() {
    private val _profileScreenState = MutableStateFlow(ProfileScreenState())
    val profileScreenState = _profileScreenState.asStateFlow()

    private val _signUpScreenState = MutableStateFlow(SignUpScreenState())
    val signUpScreenState = _signUpScreenState.asStateFlow()

    private val _loginScreenState = MutableStateFlow(LoginScreenState())
    val loginScreenState = _loginScreenState.asStateFlow()

    private val _updateScreenState = MutableStateFlow(UpdateScreenState())
    val updateScreenState = _updateScreenState.asStateFlow()

    private val _userProfileImageState = MutableStateFlow(UploadUserProfileImageState())
    val uploadUserProfileImageState = _userProfileImageState.asStateFlow()

    private val _addToCartState = MutableStateFlow(AddToCartState())
    val addToCartState = _addToCartState.asStateFlow()

    private val _getProductByIdState = MutableStateFlow(GetProductByIdState())
    val getProductByIdState = _getProductByIdState.asStateFlow()

    private val _addToFavState = MutableStateFlow(AddToFavState())
    val addToFavState = _addToFavState.asStateFlow()

    private val _getAllFavState = MutableStateFlow(GetAllFavState())
    val getAllFavState = _getAllFavState.asStateFlow()

    private val _getAllProductsState = MutableStateFlow(GetAllProductsState())
    val getAllProductsState = _getAllProductsState.asStateFlow()

    private val _getCartState = MutableStateFlow(GetCartState())
    val getCartState = _getCartState.asStateFlow()

    private val _getAllCategoriesState = MutableStateFlow(GetAllCategoriesState())
    val getAllCategoriesState = _getAllCategoriesState.asStateFlow()

    private val _getCheckoutState = MutableStateFlow(GetCheckoutState())
    val getCheckoutState = _getCheckoutState.asStateFlow()

    private val _getSpecificCategoryItemState = MutableStateFlow(GetSpecificCategoryItemState())
    val getSpecificCategoryItemState = _getSpecificCategoryItemState.asStateFlow()

    private val _getAllSuggestedProductsState = MutableStateFlow(GetAllSuggestedProductsState())
    val getAllSuggestedProductsState = _getAllSuggestedProductsState.asStateFlow()

    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()

    fun updateCartItemQuantity(cartId: String, newQty: Int) {
        viewModelScope.launch {
            repo.updateCartItemQuantity(cartId, newQty)
            getCart() // refresh cart
        }
    }

    fun removeFromCart(cartId: String) {
        viewModelScope.launch {
            repo.removeCartItem(cartId)
            getCart() // refresh cart
        }
    }


    fun uploadProfileImageToStorage(
        uri: Uri,
        userId: String,
        onResult: (String?) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("profileImages/$userId.jpg")

        storageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                onResult(downloadUrl.toString()) // ✅ Firebase image URL
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


    fun getSpecificCategoryItems(categoryName: String) {
        viewModelScope.launch {
            getSpecificCategoryItems.getSpecificCategoryItems(categoryName).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getSpecificCategoryItemState.value =
                            _getSpecificCategoryItemState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    ResultState.Loading -> {
                        _getSpecificCategoryItemState.value =
                            _getSpecificCategoryItemState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _getSpecificCategoryItemState.value =
                            _getSpecificCategoryItemState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun getCheckout(productId: String) {
        viewModelScope.launch {
            getCheckOutUseCase.getCheckOutUseCase(productId).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getCheckoutState.value =
                            _getCheckoutState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _getCheckoutState.value =
                            _getCheckoutState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _getCheckoutState.value =
                            _getCheckoutState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun getAllCategories() {
        viewModelScope.launch {
            getAllCategoryUseCase.getAllCategoriesUseCase().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllCategoriesState.value =
                            _getAllCategoriesState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _getAllCategoriesState.value =
                            _getAllCategoriesState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _getAllCategoriesState.value =
                            _getAllCategoriesState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun getCart()
    {
        viewModelScope.launch {
            getCartUseCase.getCart().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getCartState.value =
                            _getCartState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _getCartState.value =
                            _getCartState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _getCartState.value =
                            _getCartState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun getAllProducts() {
        viewModelScope.launch {
            getAllProductUseCase.getAllProducts().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllProductsState.value =
                            _getAllProductsState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _getAllProductsState.value =
                            _getAllProductsState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _getAllProductsState.value =
                            _getAllProductsState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun getAllFav() {
        viewModelScope.launch {
            getAllFavUseCase.getAllFav().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllFavState.value =
                            _getAllFavState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _getAllFavState.value =
                            _getAllFavState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _getAllFavState.value =
                            _getAllFavState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun addToFav(productDataModels: ProductDataModels) {
        viewModelScope.launch {
            addToFavUseCase.addToFav(productDataModels).collect {
                when (it) {
                    is ResultState.Error -> {
                        _addToFavState.value =
                            _addToFavState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _addToFavState.value =
                            _addToFavState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _addToFavState.value =
                            _addToFavState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun getProductsById(productId: String) {
        viewModelScope.launch {
            getProductById.getProductById(productId).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getProductByIdState.value =
                            _getProductByIdState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _getProductByIdState.value =
                            _getProductByIdState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _getProductByIdState.value =
                            _getProductByIdState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun addToCart(cartDataModels: CartDataModels) {
        viewModelScope.launch {
            addToCartUseCase.addToCart(cartDataModels).collect {
                when (it) {
                    is ResultState.Error -> {
                        _addToCartState.value =
                            _addToCartState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _addToCartState.value =
                            _addToCartState.value.copy(
                                isLoading = true
                            )

                    }

                    is ResultState.Success -> {
                        _addToCartState.value =
                            _addToCartState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    init {
        loadHomeScreenData()
    }

    fun loadHomeScreenData() {
        viewModelScope.launch {
            combine(
                getCategoryInLimitUseCase.getCategoryInLimit(),
                getProductsInLimitedUseCase.getProductsInLimited(),
                getBannerUseCase.getBannerUseCase()
            ) { categoryResult, productResult, bannerResult ->

                when {
                    categoryResult is ResultState.Error -> {
                        HomeScreenState(isLoading = false, errorMessages = categoryResult.message)
                    }

                    productResult is ResultState.Error -> {
                        HomeScreenState(isLoading = false, errorMessages = productResult.message)
                    }

                    bannerResult is ResultState.Error -> {
                        HomeScreenState(isLoading = false, errorMessages = bannerResult.message)

                    }

                    categoryResult is ResultState.Success && productResult is ResultState.Success && bannerResult is ResultState.Success -> {
                        HomeScreenState(
                            isLoading = false,
                            categories = categoryResult.data,
                            products = productResult.data,
                            banners = bannerResult.data
                        )
                    }

                    else -> {
                        HomeScreenState(isLoading = true)
                    }
                }
            }.collect { state ->
                _homeScreenState.value = state

            }
        }
    }

    fun uploadUserProfileImage(uri: Uri) {
        viewModelScope.launch {
            userProfileImageUseCase.userProfileImage(uri).collect {
                when (it) {
                    is ResultState.Error -> {
                        _userProfileImageState.value =
                            _userProfileImageState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _userProfileImageState.value =
                            _userProfileImageState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _userProfileImageState.value =
                            _userProfileImageState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun updateUserData(userDataParent: UserDataParent) {
        viewModelScope.launch {
            updateUserDataUseCase.updateUserData(userDataParent = userDataParent).collect {
                when (it) {
                    is ResultState.Error -> {
                        _updateScreenState.value =
                            _updateScreenState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _updateScreenState.value =
                            _updateScreenState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _updateScreenState.value =
                            _updateScreenState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun createUser(userData: UserData) {
        viewModelScope.launch {
            createUserUseCase.createUserUseCase(userData).collect {
                when (it) {
                    is ResultState.Error -> {
                        _signUpScreenState.value =
                            _signUpScreenState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _signUpScreenState.value =
                            _signUpScreenState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _signUpScreenState.value =
                            _signUpScreenState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun loginUser(userData: UserData) {
        viewModelScope.launch {
            loginUseCase.loginUser(userData).collect {
                when (it) {
                    is ResultState.Error -> {
                        _loginScreenState.value =
                            _loginScreenState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _loginScreenState.value =
                            _loginScreenState.value.copy(
                                isLoading = true
                            )

                    }

                    is ResultState.Success -> {
                        _loginScreenState.value =
                            _loginScreenState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun getUserById(uid: String) {
        viewModelScope.launch {
            getUserUseCase.getUserById(uid).collect {
                when (it) {
                    is ResultState.Error -> {
                        _profileScreenState.value =
                            _profileScreenState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _profileScreenState.value =
                            _profileScreenState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _profileScreenState.value =
                            _profileScreenState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }

    fun getAllSuggestedProducts() {
        viewModelScope.launch {
            getAllSuggestedProductUseCase.getAllSuggestedProducts().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllSuggestedProductsState.value =
                            _getAllSuggestedProductsState.value.copy(
                                isLoading = false,
                                errorMessages = it.message
                            )
                    }

                    is ResultState.Loading -> {
                        _getAllSuggestedProductsState.value =
                            _getAllSuggestedProductsState.value.copy(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {
                        _getAllSuggestedProductsState.value =
                            _getAllSuggestedProductsState.value.copy(
                                isLoading = false,
                                userData = it.data
                            )
                    }
                }
            }
        }
    }


}


data class ProfileScreenState(
    val isLoading: Boolean = false,
    val userData: UserDataParent? = null,
    val errorMessages: String? = null
)

data class SignUpScreenState(
    val isLoading: Boolean = false,
    val userData: String? = null,
    val errorMessages: String? = null
)

data class LoginScreenState(
    val isLoading: Boolean = false,
    val userData: String? = null,
    val errorMessages: String? = null
)

data class UpdateScreenState(
    val isLoading: Boolean = false,
    val userData: String? = null,
    val errorMessages: String? = null
)

data class UploadUserProfileImageState(
    val isLoading: Boolean = false,
    val userData: String? = null,
    val errorMessages: String? = null
)

data class AddToCartState(
    val isLoading: Boolean = false,
    val userData: String? = null,
    val errorMessages: String? = null
)

data class GetProductByIdState(
    val isLoading: Boolean = false,
    val userData: ProductDataModels? = null,
    val errorMessages: String? = null
)

data class AddToFavState(
    val isLoading: Boolean = false,
    val userData: String? = null,
    val errorMessages: String? = null
)

data class GetAllFavState(
    val isLoading: Boolean = false,
    val userData: List<ProductDataModels> = emptyList(),
    val errorMessages: String? = null
)

data class GetAllProductsState(
    val isLoading: Boolean = false,
    val userData: List<ProductDataModels> = emptyList(),
    val errorMessages: String? = null
)

data class GetCartState(
    val isLoading: Boolean = false,
    val userData: List<CartDataModels> = emptyList(),
    val errorMessages: String? = null
)

data class GetAllCategoriesState(
    val isLoading: Boolean = false,
    val userData: List<CategoryDataModels?> = emptyList(),
    val errorMessages: String? = null
)

data class GetCheckoutState(
    val isLoading: Boolean = false,
    val userData: ProductDataModels? = null,
    val errorMessages: String? = null
)

data class GetSpecificCategoryItemState(
    val isLoading: Boolean = false,
    val userData: List<ProductDataModels?> = emptyList(),
    val errorMessages: String? = null
)

data class GetAllSuggestedProductsState(
    val isLoading: Boolean = false,
    val userData: List<ProductDataModels?> = emptyList(),
    val errorMessages: String? = null
)
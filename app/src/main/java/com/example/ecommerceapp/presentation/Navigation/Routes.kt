package com.example.ecommerceapp.presentation.Navigation

import kotlinx.serialization.Serializable

sealed class SubNavigation{
    @Serializable
    object LoginSignUpScreen: SubNavigation()

    @Serializable
    object MainHomeScreen: SubNavigation()
}

sealed class Routes{
    @Serializable
    object LoginScreen

    @Serializable
    object SignUpScreen

    @Serializable
    object HomeScreen

    @Serializable
    object WishListScreen

    @Serializable
    data class CheckoutScreen(val productId: String)

    @Serializable
    object ProfileScreen

    @Serializable
    object PayScreen

    @Serializable
    object CartScreen

    @Serializable
    object SeeAllProductsScreen

    @Serializable
    object AllCategoriesScreen

    @Serializable
    data class EachProductDetailsScreen (val productId: String)

    @Serializable
    data class EachCategoryItemScreen (val categoryName: String)


}
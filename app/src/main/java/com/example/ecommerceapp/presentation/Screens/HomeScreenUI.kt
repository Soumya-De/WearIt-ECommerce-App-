package com.example.ecommerceapp.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecommerceapp.presentation.viewModels.ECommerceAppViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ecommerceapp.R
import com.example.ecommerceapp.domain.models.ProductDataModels
import com.example.ecommerceapp.presentation.Navigation.Routes
import com.example.ecommerceapp.presentation.Utils.Banner

@Composable
fun HomeScreenUI(navController: NavController, viewModel: ECommerceAppViewModel = hiltViewModel()) {
    val homeState by viewModel.homeScreenState.collectAsStateWithLifecycle()
    val getAllSuggestedProduct =
        viewModel.getAllSuggestedProductsState.collectAsStateWithLifecycle()
    val getSuggestedProductData: List<ProductDataModels> =
        getAllSuggestedProduct.value.userData.orEmpty().filterNotNull()
    LaunchedEffect(key1 = Unit) {
        viewModel.getAllSuggestedProducts()
    }
    if (homeState.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else if (homeState.errorMessages != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = homeState.errorMessages!!)
        }
    } else {
        Scaffold { paddingValues ->
            // Adjust padding values to ignore bottom padding
            val adjustedPadding = PaddingValues(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                top = paddingValues.calculateTopPadding(),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                bottom = 0.dp // Exclude bottom padding to prevent the gap
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(adjustedPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text(text = "Search") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
                // Category Section
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        Text(text = "Categories", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "See More", color = colorResource(id = R.color.teal_200),
                            modifier = Modifier.clickable {
                                navController.navigate(Routes.AllCategoriesScreen)
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(homeState.categories ?: emptyList()) { category ->
                            CategoryItem(
                                ImageUri = category.categoryImage,
                                Category = category.name,
                                onclick = {
                                    navController.navigate(Routes.EachCategoryItemScreen(category.name))
                                })
                        }
                    }
                }
                homeState.banners?.let { banners ->
                    Banner(banners = banners)
                }
                // Flash sale Section
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Flash Sale", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "See More",
                            color = colorResource(id = R.color.teal_200),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.clickable {
                                navController.navigate(Routes.SeeAllProductsScreen)
                            })
                    }
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(homeState.products ?: emptyList()) { product ->
                            ProductCard(product = product, navController = navController)
                        }
                    }
                }
                // build the suggested for you
                Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
                    when {
                        getAllSuggestedProduct.value.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        getAllSuggestedProduct.value.errorMessages != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = getAllSuggestedProduct.value.errorMessages!!)
                            }
                        }

                        getSuggestedProductData.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No Suggested Product Found")
                            }
                        }

                        else -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Suggested For You",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "See More",
                                    color = colorResource(id = R.color.teal_200),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.clickable {
                                        navController.navigate(Routes.SeeAllProductsScreen)
                                    })
                            }
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(getSuggestedProductData) { product ->
                                    ProductCard(product = product, navController = navController)
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    ImageUri: String,
    Category: String,
    onclick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .clickable { onclick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.Gray, CircleShape)
        ) {
            AsyncImage(
                model = ImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
        }
        Text(Category, style = MaterialTheme.typography.bodyMedium)
    }
}

// It is Flash Sale Section

@Composable
fun ProductCard(product: ProductDataModels, navController: NavController) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { navController.navigate(Routes.EachProductDetailsScreen(product.productId)) }
            .aspectRatio(0.7f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "₹ ${product.finalPrice}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "₹ ${product.price}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${product.avilableUnits} left)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
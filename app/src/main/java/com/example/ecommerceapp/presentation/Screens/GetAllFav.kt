package com.example.ecommerceapp.presentation.Screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecommerceapp.presentation.viewModels.ECommerceAppViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ecommerceapp.R
import com.example.ecommerceapp.domain.models.CartDataModels
import com.example.ecommerceapp.domain.models.ProductDataModels
import com.example.ecommerceapp.presentation.Navigation.Routes
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetAllFav(navController: NavController, viewModel: ECommerceAppViewModel = hiltViewModel()) {
    val getAllFav = viewModel.getAllFavState.collectAsStateWithLifecycle()
    val getAllFavData: List<ProductDataModels> = getAllFav.value.userData.orEmpty().filterNotNull()
    LaunchedEffect(key1 = Unit) {
        viewModel.getAllFav()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wish List",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            val context = LocalContext.current
            FloatingActionButton(
                onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@FloatingActionButton
                    viewModel.generateWishlistShareLink(
                        userId = userId,
                        onSuccess = { shortLink ->
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Check out my wishlist!")
                                putExtra(Intent.EXTRA_TEXT, "Here's my wishlist: $shortLink")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share via"))
                        },
                        onError = {
                            Toast.makeText(context, "Failed to generate link", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                containerColor = colorResource(id = R.color.teal_200)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share Wishlist")
            }
        }
    ) { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(text = "Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            when {
                getAllFav.value.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                getAllFav.value.errorMessages != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = getAllFav.value.errorMessages!!)
                    }
                }

                getAllFavData.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Your Wish List is Empty")
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(getAllFavData) { product ->
                            ProductCard(
                                product = product,
                                onProductClick = {
                                    navController.navigate(Routes.EachProductDetailsScreen(product.productId))
                                },
                                onRemoveFromWishlist = {
                                    Log.d("WISHLIST_UI", "Removing ${product.productId}")
                                    viewModel.removeFromFav(product.productId)
                                },
                                onMoveToCart = {
                                    val cartItem = CartDataModels(
                                        name = product.name,
                                        price = product.price,
                                        finalPrice = product.finalPrice,
                                        image = product.image,
                                        quantity = "1",
                                        size = "M",
                                        productId = product.productId,
                                        description = product.description,
                                        category = product.category
                                    )
                                    Log.d("WISHLIST_UI", "Moving ${product.productId} to cart")
                                    viewModel.addToCart(cartItem)
                                    viewModel.removeFromFav(product.productId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductDataModels,
    onProductClick: () -> Unit,
    onRemoveFromWishlist: () -> Unit,
    onMoveToCart: () -> Unit
) {
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onProductClick,
        ) {
            Column {
                Box {
                    AsyncImage(
                        model = product.image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = onRemoveFromWishlist,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove")
                    }
                }
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹${product.finalPrice}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = onMoveToCart,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Move to Cart")
                    }
                }
            }
        }
    }
}
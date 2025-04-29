package com.example.ecommerceapp.presentation.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.presentation.viewModels.ECommerceAppViewModel
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ecommerceapp.domain.models.ProductDataModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecommerceapp.domain.models.CommentModel
import com.example.ecommerceapp.presentation.Navigation.Routes
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedWishlistScreen(userId: String, navController: NavController, viewModel: ECommerceAppViewModel = hiltViewModel()) {
    val sharedWishlistState = viewModel.sharedWishlistState.collectAsStateWithLifecycle()
    LaunchedEffect(userId) {
        viewModel.getSharedWishlist(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Shared Wishlist",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                sharedWishlistState.value.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                sharedWishlistState.value.errorMessages != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = sharedWishlistState.value.errorMessages ?: "Unknown error")
                    }
                }
                sharedWishlistState.value.userData.isNullOrEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No items in shared wishlist")
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sharedWishlistState.value.userData.orEmpty()) { product ->
                            val commentList = remember { mutableStateListOf<CommentModel>() }

                            LaunchedEffect(product.productId) {
                                viewModel.getComments(userId, product.productId).collect {
                                    commentList.clear()
                                    commentList.addAll(it)
                                }
                            }

                            val firebaseAuth = remember { FirebaseAuth.getInstance() }

                            // ✅ This Column will wrap both the product card and comments
                            Column(modifier = Modifier.fillMaxWidth()) {

                                ReadOnlyProductCard(
                                    product = product,
                                    userId = userId,
                                    viewModel = viewModel,
                                    navController = navController, // pass here
                                    commentCount = commentList.size,
                                    onProductClick = {
                                        navController.navigate(Routes.EachProductDetailsScreen(product.productId))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ReadOnlyProductCard(
    product: ProductDataModels,
    onProductClick: () -> Unit,
    userId: String,
    viewModel: ECommerceAppViewModel,
    navController: NavController,
    commentCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
    ) {
        Column {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${product.finalPrice}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "₹${product.price}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 💖 Likes row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            viewModel.likeProduct(userId, product.productId)
                        }) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Like",
                                tint = Color.Red
                            )
                        }
                        Text(
                            text = "${product.likes?.get("total") ?: 0}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    TextButton(
                        onClick = {
                            navController.navigate(Routes.CommentsScreen(productId = product.productId, ownerId = userId))
                        }
                    ) {
                        Text("💬 $commentCount")
                    }
                }
            }
        }
    }
}


@Composable
fun CommentsSection(
    comments: List<CommentModel>,
    onCommentSubmit: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }

    Column {
        Text("Comments", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        LazyColumn(modifier = Modifier.height(120.dp)) {
            items(comments) { comment ->
                Text(
                    text = "${comment.user}: ${comment.text}",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Add Comment") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (input.isNotBlank()) {
                    onCommentSubmit(input)
                    input = ""
                }
            },
            modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
        ) {
            Text("Send")
        }
    }
}


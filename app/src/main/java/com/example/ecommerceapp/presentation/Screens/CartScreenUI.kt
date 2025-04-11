package com.example.ecommerceapp.presentation.Screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ecommerceapp.R
import com.example.ecommerceapp.domain.models.CartDataModels
import com.example.ecommerceapp.presentation.Navigation.BottomNavItem
import com.example.ecommerceapp.presentation.Navigation.Routes
import com.example.ecommerceapp.presentation.viewModels.ECommerceAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, viewModel: ECommerceAppViewModel = hiltViewModel()) {
    val cartState = viewModel.getCartState.collectAsStateWithLifecycle()
    val cartData = cartState.value.userData ?: emptyList()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val productIds = cartData.map { it.productId }
    val totalPrice = cartData.sumOf {
        it.price.toIntOrNull()?.times(it.quantity.toIntOrNull() ?: 1) ?: 0
    }


    LaunchedEffect(key1 = Unit) {
        viewModel.getCart()
    }
    Scaffold(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cart",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerpadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {
            when {
                cartState.value.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                cartState.value.errorMessages != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text("Sorry, Unable to get information")
                    }
                }

                cartData.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Products Available")
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Items",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(.45f))
                            Text(
                                text = "Details",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(.1f))
                            Text(
                                text = "QTY",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(.15f))
                        }
                        LazyColumn(
                            modifier = Modifier.weight(.6f)

                        ) {
                            items(cartData) { item ->
                                CartItemCard(
                                    item = item,
                                    onIncrease = {
                                        val currentQty = item.quantity.toIntOrNull() ?: 1
                                        if (currentQty < 99) { // Optional max limit
                                            Log.d("CART_UI", "Increase ${item.productId} from $currentQty to ${currentQty + 1}")
                                            viewModel.updateCartItemQuantity(item.productId, currentQty + 1)
                                        }
                                    },
                                    onDecrease = {
                                        val currentQty = item.quantity.toIntOrNull() ?: 1
                                        if (currentQty > 1) {
                                            Log.d("CART_UI", "Decrease ${item.productId} from $currentQty to ${currentQty - 1}")
                                            viewModel.updateCartItemQuantity(item.productId, currentQty - 1)
                                        } else {
                                            Log.d("CART_UI", "Qty is 1, cannot decrease more.")
                                        }
                                    },
                                    onRemove = {
                                        Log.d("CART_UI", "Remove ${item.productId}")
                                        viewModel.removeFromCart(item.productId)
                                    }
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                        val totalOriginal = cartData.sumOf {
                            it.price.toIntOrNull()?.times(it.quantity.toIntOrNull() ?: 1) ?: 0
                        }
                        val totalFinal = cartData.sumOf {
                            it.finalPrice.toIntOrNull()?.times(it.quantity.toIntOrNull() ?: 1) ?: 0
                        }
                        val totalDiscount = totalOriginal - totalFinal

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Total MRP: ₹$totalOriginal",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Discount on MRP: -₹$totalDiscount",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Amount Payable: ₹$totalFinal",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF388E3C),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (totalDiscount > 0) {
                                Text(
                                    text = "You saved ₹$totalDiscount! 🎉",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                        Button(
                            onClick = {navController.navigate(Routes.CheckoutScreen(productIds))},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(colorResource(id = R.color.teal_200))
                        ) {
                            Text(text = "Checkout")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartDataModels,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.image,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Size: ${item.size}")
                Text(text = "₹${item.price}")
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                    }

                    Text(text = item.quantity)

                    IconButton(onClick = onIncrease) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                    }
                }

                IconButton(onClick = onRemove) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove Item")
                }
            }
        }
    }
}
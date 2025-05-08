package com.example.ecommerceapp.presentation.Screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecommerceapp.presentation.viewModels.ECommerceAppViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ecommerceapp.R
import com.example.ecommerceapp.domain.models.ProductDataModels
import com.example.ecommerceapp.presentation.Navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutScreen(
    navController: NavController,
    screen: Routes.CheckoutScreen, // Contains List<String>
    viewModel: ECommerceAppViewModel = hiltViewModel(),
    pay: () -> Unit
) {

    val state = viewModel.getProductByIdState.collectAsStateWithLifecycle()
    val email = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val city = remember { mutableStateOf("") }
    val zipCode = remember { mutableStateOf("") }
    val selectedMethod = remember { mutableStateOf("Standard FREE Delivery Over Rs. 4500") }
    val productList = remember { mutableStateListOf<ProductDataModels>() }

    LaunchedEffect(screen.productIds) {
        productList.clear()
        screen.productIds.forEach { productId ->
            viewModel.getProductsById(productId)
        }
    }
    val fetchedProduct = state.value.userData
    LaunchedEffect(fetchedProduct) {
        fetchedProduct?.let {
            if (!productList.any { p -> p.productId == it.productId }) {
                productList.add(it)
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Shipping") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        // Adjust padding values to ignore bottom padding
        val adjustedPadding = PaddingValues(
            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
            top = paddingValues.calculateTopPadding(),
            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
            bottom = 0.dp // Exclude bottom padding to prevent the gap
        )
        when {
            state.value.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.value.errorMessages != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(text = "Sorry, Unable To Get Information")
                }
            }

            state.value.userData == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Products Available")
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(adjustedPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text("Your Products", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    productList.forEach { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = product.image,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(product.name, fontWeight = FontWeight.Bold)
                                    Text("₹${product.finalPrice}")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Column {
                        Text(
                            text = "Contact Information",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column {
                        Text(
                            text = "Shipping Address",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = city.value,
                            onValueChange = { city.value = it },
                            label = { Text("Country/Region") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            OutlinedTextField(
                                value = firstName.value,
                                onValueChange = { firstName.value = it },
                                label = { Text("First Name") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            OutlinedTextField(
                                value = lastName.value,
                                onValueChange = { lastName.value = it },
                                label = { Text("Last Name") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = address.value,
                            onValueChange = { address.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Address") })
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            OutlinedTextField(
                                value = city.value,
                                onValueChange = { city.value = it },
                                label = { Text("Country") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            OutlinedTextField(
                                value = zipCode.value,
                                onValueChange = { zipCode.value = it },
                                label = { Text("Zip Code") },
                                modifier = Modifier
                                    .weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Column {
                        Text(
                            text = "Shipping Method",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedMethod.value == "Standard FREE Delivery Over Rs. 4500",
                                onClick = {
                                    selectedMethod.value = "Standard FREE Delivery Over Rs. 4500"
                                })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Standard FREE Delivery Over Rs. 4500")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedMethod.value == "Cash On Delivery Rs. 50",
                                onClick = {
                                    selectedMethod.value = "Cash On Delivery Rs. 50"
                                })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Cash On Delivery Rs. 50")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            pay.invoke()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(colorResource(id = R.color.teal_200))
                    ) {
                        Text(text = "Continue To Shipping")
                    }
                }
            }
        }
    }
}
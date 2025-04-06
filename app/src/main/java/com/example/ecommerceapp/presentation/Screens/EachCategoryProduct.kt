package com.example.ecommerceapp.presentation.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecommerceapp.presentation.viewModels.ECommerceAppViewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.presentation.Navigation.Routes
import com.example.ecommerceapp.presentation.Utils.ProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EachCategoryProduct(
    navController: NavController,
    categoryName: String,
    viewModel: ECommerceAppViewModel = hiltViewModel()
) {
    val state = viewModel.getSpecificCategoryItemState.collectAsStateWithLifecycle()
    val products = state.value.userData ?: emptyList()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    LaunchedEffect(key1 = Unit) {
        viewModel.getSpecificCategoryItems(categoryName)
    }
    when {
        state.value.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.value.errorMessages}")
            }
        }

        products.isEmpty() -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.value.errorMessages}")
            }
        }

        else -> {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title =
                            {
                                Text(
                                    text = categoryName,
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
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        placeholder = { Text(text = "Search") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(products) { product ->
                            ProductItem(product = product!!, onProductClick = {
                                navController.navigate(Routes.EachProductDetailsScreen(product.productId))
                            })
                        }
                    }
                }
            }
        }
    }
}
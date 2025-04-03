package com.example.ecommerceapp.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.ecommerceapp.R
import com.example.ecommerceapp.presentation.Navigation.Routes
import com.example.ecommerceapp.presentation.Utils.SuccessAlertDialog
import com.example.ecommerceapp.presentation.Navigation.SubNavigation
import com.example.ecommerceapp.presentation.Utils.CustomTextField
import com.example.ecommerceapp.presentation.viewModels.ECommerceAppViewModel


@Composable
fun LoginScreenUI(
    navController: NavHostController,
    viewModel: ECommerceAppViewModel = hiltViewModel()
) {

    val state = viewModel.loginScreenState.collectAsStateWithLifecycle()
    val showDialog = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    if (state.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

        }
    } else if (state.value.errorMessages != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = state.value.errorMessages!!)
        }
    } else if (state.value.userData != null) {
        SuccessAlertDialog(onClick = {
            navController.navigate(SubNavigation.MainHomeScreen)
        })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Log In",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 16.dp)
            )
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                visualTransformation = PasswordVisualTransformation()
            )
            Text(
                text = "Forgot Password?",
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.End),
                style = TextStyle(color = colorResource(id = R.color.teal_200))
            )
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.teal_200)),
                enabled = email.isNotBlank() && password.isNotBlank(),
                border = BorderStroke(1.dp, colorResource(id = R.color.teal_200)),
                content = { Text(text = "Log In") })

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
            ) {
                Text("Don't have an account?")
                TextButton(onClick = {
                    navController.navigate(Routes.SignUpScreen)
                }) {
                    Text(text = "Sign Up", color = colorResource(id = R.color.teal_200))
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "Or",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }


            OutlinedButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.google_logo
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Log in with Google")
            }
        }

    }
}

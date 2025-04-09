package com.example.ecommerceapp.presentation.Screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ecommerceapp.presentation.viewModels.ECommerceAppViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.ecommerceapp.R
import com.example.ecommerceapp.domain.models.UserData
import com.example.ecommerceapp.domain.models.UserDataParent
import com.example.ecommerceapp.presentation.Navigation.SubNavigation
import com.example.ecommerceapp.presentation.Utils.LogoutAlertDialog
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ProfilesScreenUI(
    navController: NavController,
    firebaseAuth: FirebaseAuth,
    viewModel: ECommerceAppViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        viewModel.getUserById(firebaseAuth.currentUser!!.uid)
    }
    val profileScreenState = viewModel.profileScreenState.collectAsStateWithLifecycle()
    val updateScreenState = viewModel.updateScreenState.collectAsStateWithLifecycle()
    val userProfileImageState = viewModel.uploadUserProfileImageState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentUser = firebaseAuth.currentUser
    Log.d("UPLOAD", "UserID = ${currentUser?.uid}")
    val showDialog = remember { mutableStateOf(false) }
    val isEditing = remember { mutableStateOf(false) }
    val imageUri = rememberSaveable { mutableStateOf("") }
    val firstName =
        remember { mutableStateOf(profileScreenState.value.userData?.userdata?.firstName ?: "") }
    val lastName =
        remember { mutableStateOf(profileScreenState.value.userData?.userdata?.lastName ?: "") }
    val email =
        remember { mutableStateOf(profileScreenState.value.userData?.userdata?.email ?: "") }
    val phoneNumber =
        remember { mutableStateOf(profileScreenState.value.userData?.userdata?.phoneNumber ?: "") }
    val address =
        remember { mutableStateOf(profileScreenState.value.userData?.userdata?.address ?: "") }
    LaunchedEffect(profileScreenState.value.userData) {
        profileScreenState.value.userData?.userdata?.let { userData ->
            firstName.value = userData.firstName ?: ""
            lastName.value = userData.lastName ?: ""
            email.value = userData.email ?: ""
            phoneNumber.value = userData.phoneNumber ?: ""
            address.value = userData.address ?: ""
            imageUri.value = userData.profileImage ?: ""
        }
    }
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null && currentUser != null) {
                viewModel.uploadProfileImageToStorage(uri, currentUser.uid) { downloadUrl ->
                    if (downloadUrl != null) {
                        imageUri.value = downloadUrl // ✅ Save valid URL
                    } else {
                        Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )
    if (updateScreenState.value.userData != null) {
        Toast.makeText(context, updateScreenState.value.userData, Toast.LENGTH_SHORT).show()
    } else if (updateScreenState.value.errorMessages != null) {
        Toast.makeText(context, updateScreenState.value.errorMessages, Toast.LENGTH_SHORT).show()

    } else if (updateScreenState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

        }
    }
    if (userProfileImageState.value.userData != null) {
        imageUri.value = userProfileImageState.value.userData.toString()
    } else if (userProfileImageState.value.errorMessages != null) {
        Toast.makeText(context, userProfileImageState.value.errorMessages, Toast.LENGTH_SHORT)
            .show()
    } else if (userProfileImageState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (profileScreenState.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

    } else if (profileScreenState.value.errorMessages != null) {
        Text(text = profileScreenState.value.errorMessages!!)

    } else if (profileScreenState.value.userData != null) {
        Scaffold() { innerpadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerpadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            )
            {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Start)
                ) {
                    SubcomposeAsyncImage(
                        model = if (isEditing.value) imageUri.value else imageUri.value,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, color = colorResource(id = R.color.teal_200), CircleShape)
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> CircularProgressIndicator()
                            is AsyncImagePainter.State.Error -> Icon(
                                Icons.Default.Person,
                                contentDescription = null
                            )

                            else -> SubcomposeAsyncImageContent()
                        }
                    }
                    if (isEditing.value) {
                        IconButton(
                            onClick = {
                                pickMedia.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomEnd)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Change Picture",
                                tint = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Row {
                    OutlinedTextField(
                        value = firstName.value,
                        onValueChange = { firstName.value = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = colorResource(id = R.color.teal_200),
                            focusedBorderColor = colorResource(id = R.color.teal_200)
                        ),
                        modifier = Modifier.weight(1f),
                        readOnly = !isEditing.value,
                        shape = RoundedCornerShape(10.dp),
                        label = { Text(text = "First Name") }
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    OutlinedTextField(
                        value = lastName.value,
                        onValueChange = { lastName.value = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = colorResource(id = R.color.teal_200),
                            focusedBorderColor = colorResource(id = R.color.teal_200)
                        ),
                        modifier = Modifier.weight(1f),
                        readOnly = !isEditing.value,
                        shape = RoundedCornerShape(10.dp),
                        label = { Text(text = "Last Name") }
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.teal_200),
                        focusedBorderColor = colorResource(id = R.color.teal_200)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditing.value,
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Email") }
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    value = phoneNumber.value,
                    onValueChange = { phoneNumber.value = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.teal_200),
                        focusedBorderColor = colorResource(id = R.color.teal_200)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditing.value,
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Phone Number") })
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedTextField(
                    value = address.value,
                    onValueChange = { address.value = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = colorResource(id = R.color.teal_200),
                        focusedBorderColor = colorResource(id = R.color.teal_200)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditing.value,
                    shape = RoundedCornerShape(10.dp),
                    label = { Text(text = "Address") }
                )
                Spacer(modifier = Modifier.size(16.dp))
                OutlinedButton(
                    onClick = { showDialog.value = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.teal_200))
                ) {
                    Text(text = "Log Out")
                }
                if (showDialog.value) {
                    LogoutAlertDialog(
                        onDismiss = { showDialog.value = false },
                        onConfirm = {
                            firebaseAuth.signOut()
                            navController.navigate(SubNavigation.LoginSignUpScreen)
                        }
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                if (!isEditing.value) {
                    OutlinedButton(
                        onClick = {
                            isEditing.value = !isEditing.value
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Edit Profile")
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            val updatedUserData = UserData(
                                firstName = firstName.value,
                                lastName = lastName.value,
                                email = email.value,
                                phoneNumber = phoneNumber.value,
                                address = address.value,
                                profileImage = imageUri.value
                            )
                            val userDataParent = UserDataParent(
                                nodeId = profileScreenState.value.userData!!.nodeId,
                                userdata = updatedUserData
                            )
                            viewModel.updateUserData(userDataParent)
                            isEditing.value = !isEditing.value
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Save Profile")
                    }
                }
            }
        }
    }
}
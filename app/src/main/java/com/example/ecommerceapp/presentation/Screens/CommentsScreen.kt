package com.example.ecommerceapp.presentation.Screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ecommerceapp.domain.models.CommentModel
import com.example.ecommerceapp.presentation.viewModels.ECommerceAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommentsScreen(
    productId: String,
    ownerId: String,
    viewModel: ECommerceAppViewModel = hiltViewModel(),
    navController: NavController
) {
    val commentList = remember { mutableStateListOf<CommentModel>() }
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    var showDialog by remember { mutableStateOf(false) }
    var selectedComment by remember { mutableStateOf<CommentModel?>(null) }


    LaunchedEffect(productId) {
        viewModel.getComments(ownerId, productId).collect {
            commentList.clear()
            commentList.addAll(it)
        }
    }

    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comments") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(commentList.orEmpty()) { comment ->
                    val color = remember(comment.user) {
                        val colors = listOf(Color.Blue, Color.Green, Color.Red, Color.Magenta, Color.Cyan)
                        colors[comment.user.hashCode().absoluteValue % colors.size]
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .combinedClickable(
                                onClick = { /* normal click, do nothing */ },
                                onLongClick = {
                                    if (comment.userId == firebaseAuth.currentUser?.uid) { // You MUST match using UID, not name.
                                        selectedComment = comment
                                        showDialog = true
                                    }
                                }
                            )
                    ) {
                        Text(
                            text = "${comment.user}: ",
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = comment.text)
                    }
                }
            }
            if (showDialog && selectedComment != null) {
                showDeleteDialog(
                    onDelete = {
                        viewModel.deleteComment(ownerId, productId, selectedComment!!)
                        showDialog = false
                        selectedComment = null
                    },
                    onDismiss = {
                        showDialog = false
                        selectedComment = null
                    }
                )
            }

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Add a comment") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        val currentUserId = firebaseAuth.currentUser?.uid

                        if (currentUserId != null) {
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(currentUserId)
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val firstName = documentSnapshot.getString("firstName") ?: "Guest"

                                    viewModel.addCommentToProduct(
                                        userId = ownerId,
                                        productId = productId,
                                        comment = CommentModel(text = input, user = firstName)
                                    )
                                    input = ""
                                }
                                .addOnFailureListener {
                                    Log.e("COMMENT", "Failed to fetch user first name: ${it.message}")
                                }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun showDeleteDialog(onDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Delete Comment") },
        text = { Text("Are you sure you want to delete this comment?") },
        confirmButton = {
            TextButton(onClick = { onDelete() }) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
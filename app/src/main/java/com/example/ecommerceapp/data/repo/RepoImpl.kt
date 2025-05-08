package com.example.ecommerceapp.data.repo

import android.net.Uri
import android.util.Log
import com.example.ecommerceapp.common.ADD_TO_CART
import com.example.ecommerceapp.common.ResultState
import com.example.ecommerceapp.common.USER_COLLECTION
import com.example.ecommerceapp.common.PRODUCTS_COLLECTION
import com.example.ecommerceapp.common.ADD_TO_FAV
import com.example.ecommerceapp.domain.models.BannerDataModels
import com.example.ecommerceapp.domain.models.CartDataModels
import com.example.ecommerceapp.domain.models.CategoryDataModels
import com.example.ecommerceapp.domain.models.CommentModel
import com.example.ecommerceapp.domain.models.ProductDataModels
import com.example.ecommerceapp.domain.models.UserData
import com.example.ecommerceapp.domain.models.UserDataParent
import com.example.ecommerceapp.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


class RepoImpl @Inject constructor(
    var firebaseAuth: FirebaseAuth, var firebaseFirestore: FirebaseFirestore
) : Repo {

    override fun getComments(userId: String, productId: String): Flow<List<CommentModel>> {
        return firebaseFirestore.collection("ADD_TO_FAV")
            .document(userId)
            .collection("User_Fav")
            .document(productId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .let { ref ->
                callbackFlow {
                    val listener = ref.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val comments = snapshot?.documents?.mapNotNull {
                            it.toObject(CommentModel::class.java)
                        } ?: emptyList()

                        trySend(comments).isSuccess
                    }

                    awaitClose { listener.remove() }
                }
            }
    }


    override fun getWishlistForUser(userId: String): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("add_to_fav")
            .document(userId)
            .collection("User_Fav")
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(ProductDataModels::class.java) }
                trySend(ResultState.Success(list))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Unknown error"))
            }

        awaitClose { close() }
    }


    override fun removeFromFav(productId: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val userId = firebaseAuth.currentUser?.uid ?: return@callbackFlow

        firebaseFirestore.collection(ADD_TO_FAV)
            .document(userId)
            .collection("User_Fav")
            .document(productId) // <- matches what you set above
            .delete()
            .addOnSuccessListener {
                trySend(ResultState.Success("Removed from Wishlist"))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Unknown error"))
            }

        awaitClose { close() }
    }

    override fun RegisterUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseAuth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseFirestore.collection(USER_COLLECTION)
                            .document(it.result.user?.uid.toString()).set(userData)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    trySend(ResultState.Success("User Registered Successfully and add to Firestore"))
                                } else {
                                    if (it.exception != null) {
                                        trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                                    }
                                }
                            }
                        trySend(ResultState.Success("User Registered Successfully"))
                    } else {
                        if (it.exception != null) {
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }
                    }
                }
            awaitClose {
                close()
            }
        }

    override fun LoginUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseAuth.signInWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(ResultState.Success("User Logged In Successfully"))
                    } else {
                        if (it.exception != null) {
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }
                    }
                }
            awaitClose {
                close()
            }
        }

    override fun getUserById(uid: String): Flow<ResultState<UserDataParent>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(USER_COLLECTION).document(uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val data = it.result.toObject(UserData::class.java)!!
                val userDataParent = UserDataParent(it.result.id, data)
                trySend(ResultState.Success(userDataParent))
            } else {
                if (it.exception != null) {
                    trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                }
            }
        }
        awaitClose {
            close()
        }
    }

    override fun getProductLikes(userId: String, productId: String): Flow<Map<String, Any>> = callbackFlow {
        val docRef = firebaseFirestore
            .collection("ADD_TO_FAV")
            .document(userId)
            .collection("User_Fav")
            .document(productId)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val likes = snapshot?.get("likes") as? Map<String, Any> ?: emptyMap()
            trySend(likes).isSuccess
        }

        awaitClose { listener.remove() }
    }


    override fun updateUserData(userDataParent: UserDataParent): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection(USER_COLLECTION).document(userDataParent.nodeId)
                .update(userDataParent.userdata.toMap()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(ResultState.Success("User Data Updated Successfully"))
                    } else {
                        if (it.exception != null) {
                            trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                        }
                    }
                }
            awaitClose {
                close()
            }
        }

    override fun userProfileImage(uri: Uri): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        FirebaseStorage.getInstance().reference.child("userProfileImage/${System.currentTimeMillis()}+${firebaseAuth.currentUser?.uid}")
            .putFile(uri ?: Uri.EMPTY).addOnCompleteListener {
                it.result.storage.downloadUrl.addOnSuccessListener { imageUri ->
                    trySend(ResultState.Success(imageUri.toString()))
                }
                if (it.exception != null) {
                    trySend(ResultState.Error(it.exception?.localizedMessage.toString()))
                }
            }
        awaitClose {
            close()
        }
    }

    override fun getCategoriesInLimited(): Flow<ResultState<List<CategoryDataModels>>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection("Categories").limit(7).get()
                .addOnSuccessListener { querySnapShot ->
                    val categories = querySnapShot.documents.mapNotNull { document ->
                        document.toObject(CategoryDataModels::class.java)
                    }
                    trySend(ResultState.Success(categories))
                }.addOnFailureListener { exception ->
                    trySend(ResultState.Error(exception.toString()))
                }
            awaitClose {
                close()

            }
        }

    override fun getProductsInLimited(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Products").limit(10).get().addOnSuccessListener {
            val products = it.documents.mapNotNull { document ->
                document.toObject(ProductDataModels::class.java)?.apply {
                    productId = document.id
                }
            }
            trySend(ResultState.Success(products))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()
        }
    }

    override fun getAllProducts(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Products").get().addOnSuccessListener {
            val products = it.documents.mapNotNull { document ->
                document.toObject(ProductDataModels::class.java)?.apply {
                    productId = document.id
                }
            }
            trySend(ResultState.Success(products))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()
        }
    }

    override fun getProductById(productId: String): Flow<ResultState<ProductDataModels>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection(PRODUCTS_COLLECTION).document(productId).get()
                .addOnSuccessListener { documentSnapshot ->
                    Log.d(
                        "FIREBASE",
                        "Fetched doc: ${documentSnapshot.id}, exists: ${documentSnapshot.exists()}"
                    )
                    val product = documentSnapshot.toObject(ProductDataModels::class.java)
                    if (product != null) {
                        Log.d("FIREBASE", "Product parsed: ${product.name}")
                        val productWithId = product.copy(productId = documentSnapshot.id)
                        trySend(ResultState.Success(productWithId))
                    } else {
                        Log.e("FIREBASE", "Product is null!")
                        trySend(ResultState.Error("Product not found or data is null"))
                    }
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Unknown error"))
                }
            awaitClose { close() }
        }


    override fun addToCart(cartDataModels: CartDataModels): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)

            val userId = firebaseAuth.currentUser?.uid ?: run {
                trySend(ResultState.Error("User not logged in"))
                close()
                return@callbackFlow
            }

            // ✅ Set document ID to productId
            firebaseFirestore.collection("add_to_cart")
                .document(userId)
                .collection("User_Cart")
                .document(cartDataModels.productId) // 👈 this ensures document ID is productId
                .set(cartDataModels)
                .addOnSuccessListener {
                    trySend(ResultState.Success("Product Added to Cart"))
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }

            awaitClose { close() }
        }

    override fun likeProduct(userId: String, productId: String): Flow<ResultState<Unit>> = callbackFlow {
        val ref = firebaseFirestore.collection(ADD_TO_FAV)
            .document(userId)
            .collection("User_Fav")
            .document(productId)

        firebaseFirestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val currentLikes = snapshot.get("likes.total") as? Long ?: 0
            transaction.update(ref, "likes.total", currentLikes + 1)
        }.addOnSuccessListener {
            trySend(ResultState.Success(Unit))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message ?: "Like failed"))
        }

        awaitClose { close() }
    }


    override fun addTOFav(productDataModels: ProductDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val userId = firebaseAuth.currentUser?.uid ?: return@callbackFlow

        val favRef = firebaseFirestore.collection(ADD_TO_FAV)
            .document(userId)
            .collection("User_Fav")
            .document(productDataModels.productId)

        favRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                // First-time add – include likes initialized to 0
                val data = hashMapOf(
                    "productId" to productDataModels.productId,
                    "name" to productDataModels.name,
                    "image" to productDataModels.image,
                    "price" to productDataModels.price,
                    "finalPrice" to productDataModels.finalPrice,
                    "description" to productDataModels.description,
                    "category" to productDataModels.category,
                    "likes" to mapOf("total" to 0)
                )
                favRef.set(data)
                    .addOnSuccessListener {
                        trySend(ResultState.Success("Product Added to Fav"))
                    }
                    .addOnFailureListener {
                        trySend(ResultState.Error(it.message ?: "Failed to add to fav"))
                    }
            } else {
                trySend(ResultState.Success("Already in Favorites"))
            }
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message ?: "Failed to fetch fav document"))
        }

        awaitClose { close() }
    }


    override fun getAllFav(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection(ADD_TO_FAV).document(firebaseAuth.currentUser!!.uid)
            .collection("User_Fav").get().addOnSuccessListener {
                val fav = it.documents.mapNotNull { document ->
                    document.toObject(ProductDataModels::class.java)
                }
                trySend(ResultState.Success(fav))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.toString()))
            }
        awaitClose {
            close()
        }
    }

    override fun getCart(): Flow<ResultState<List<CartDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("add_to_cart")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("User_Cart")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val cartItems = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(CartDataModels::class.java)?.copy(cartId = document.id)
                }
                trySend(ResultState.Success(cartItems))
            }

        awaitClose {
            close()
        }
    }

    override fun getAllCategories(): Flow<ResultState<List<CategoryDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Categories").get().addOnSuccessListener {
            val categories = it.documents.mapNotNull { document ->
                document.toObject(CategoryDataModels::class.java)
            }
            trySend(ResultState.Success(categories))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()
        }

    }

    override fun getCheckout(productId: String): Flow<ResultState<ProductDataModels>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection("Products").document(productId).get()
                .addOnSuccessListener {
                    val product = it.toObject(ProductDataModels::class.java)
                    trySend(ResultState.Success(product!!))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }
        }

    override fun getBanner(): Flow<ResultState<List<BannerDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        firebaseFirestore.collection("Banners").get().addOnSuccessListener {
            val banner = it.documents.mapNotNull { document ->
                document.toObject(BannerDataModels::class.java)
            }
            trySend(ResultState.Success(banner))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.toString()))
        }
        awaitClose {
            close()
        }
    }

    override fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductDataModels>>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection("Products").whereEqualTo("category", categoryName).get()
                .addOnSuccessListener {
                    val products = it.documents.mapNotNull { document ->
                        document.toObject(ProductDataModels::class.java)?.apply {
                            productId = document.id
                        }
                    }
                    trySend(ResultState.Success(products))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }
        }

    override fun getAllSuggestedProducts(): Flow<ResultState<List<ProductDataModels>>> =
        callbackFlow {
            trySend(ResultState.Loading)
            firebaseFirestore.collection(ADD_TO_FAV).document(firebaseAuth.currentUser!!.uid)
                .collection("User_Fav").get().addOnSuccessListener {
                    val fav = it.documents.mapNotNull { document ->
                        document.toObject(ProductDataModels::class.java)
                    }
                    trySend(ResultState.Success(fav))
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.toString()))
                }
            awaitClose {
                close()
            }
        }

    override suspend fun updateCartItemQuantity(productId: String, newQty: Int) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        Log.d("FIRESTORE", "Updating cart item $productId to qty $newQty")

        firebaseFirestore.collection("add_to_cart")
            .document(userId)
            .collection("User_Cart")
            .document(productId)
            .update("quantity", newQty.toString())  // Only if quantity is a string in Firestore
            .addOnSuccessListener {
                Log.d("FIRESTORE", "Quantity update success for $productId")
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Quantity update failed for $productId: ${it.message}")
            }
    }

    override suspend fun removeCartItem(productId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        Log.d("FIRESTORE", "Removing cart item $productId")

        firebaseFirestore.collection("add_to_cart")
            .document(userId)
            .collection("User_Cart")
            .document(productId)
            .delete()
            .addOnSuccessListener {
                Log.d("FIRESTORE", "Remove success for $productId")
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Remove failed for $productId: ${it.message}")
            }
    }


}
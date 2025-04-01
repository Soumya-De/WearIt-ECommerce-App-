package com.example.ecommerceapp.domain.models

data class UserData(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val profileImage: String = "",
) {
    fun toMap(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["firstName"] = firstName
        map["lastName"] = lastName
        map["email"] = email
        map["password"] = password
        map["phoneNumber"] = phoneNumber
        map["address"] = address
        map["profileImage"] = profileImage
        return map
    }
}

data class UserDataParent(
    val nodeId: String = "",
    val userdata: UserData = UserData()
)
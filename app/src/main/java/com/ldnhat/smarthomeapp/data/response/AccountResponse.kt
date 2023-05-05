package com.ldnhat.smarthomeapp.data.response

data class AccountResponse(
    val id : String?,
    val login : String?,
    val firstName : String?,
    val lastName : String?,
    val email : String?,
    val imageUrl : String?,
    val activated : Boolean?,
    val langKey : String?,
    val createdBy: String,
    val createdDate: String,
    val lastModifiedBy: String,
    val lastModifiedDate: String
)

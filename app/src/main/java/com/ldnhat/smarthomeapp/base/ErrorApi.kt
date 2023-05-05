package com.ldnhat.smarthomeapp.base

data class ErrorApi(
    val entityName: String?,
    val errorKey: String?,
    val type: String?,
    val title: String?,
    val status: Int?,
    val message: String?,
    val params: String?
) {
}
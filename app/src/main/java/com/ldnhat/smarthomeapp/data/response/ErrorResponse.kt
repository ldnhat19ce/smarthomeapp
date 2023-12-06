package com.ldnhat.smarthomeapp.data.response

data class ErrorResponse(
    val entityName: String?,
    val errorKey: String?,
    val title: String?,
    val message: String?,
    val status: String?,
    val type: String?,
    val params: String?
) {
    override fun toString(): String {
        return "entityName: $entityName | errorKey: $errorKey | title: $title | message: $message | type: $type | params: $params | errorKey: $errorKey"
    }
}

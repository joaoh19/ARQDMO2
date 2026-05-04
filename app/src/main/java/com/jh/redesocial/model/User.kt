package com.jh.redesocial.model

data class User(
    val email: String = "",
    val username: String = "",
    val nomeCompleto: String = "",
    val fotoPerfil: String = "" // String em Base64
)
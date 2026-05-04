package com.jh.redesocial.model

import android.graphics.Bitmap

data class Post(
    val descricao: String = "",
    val foto: Bitmap? = null, // Pode ser null se usarmos a String
    val imageString: String = "" // Adiciona este campo para o Glide usar
)
package com.jh.redesocial.model

import android.graphics.Bitmap

/**
 * Classe de modelo que representa um Post na rede social.
 * Conforme o PDF, o post no RecyclerView exibe imagem e descrição[cite: 38].
 */
data class Post(
    val descricao: String,
    val foto: Bitmap?
)
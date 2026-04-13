package com.jh.redesocial.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.jh.redesocial.databinding.ActivityPostBinding
import com.jh.redesocial.util.Base64Converter

class PostActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPostBinding.inflate(layoutInflater) }
    private val db by lazy { FirebaseFirestore.getInstance() }

    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.imgPostPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSelecionarImagem.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnPublicar.setOnClickListener {
            publicarPost()
        }
    }

    private fun publicarPost() {
        val descricao = binding.edtDescricaoPost.text.toString()

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Escreva uma legenda!", Toast.LENGTH_SHORT).show()
            return
        }

        // Converte a imagem do ImageView para String Base64
        val imageString = Base64Converter.drawableToString(binding.imgPostPreview.drawable)

        val post = hashMapOf(
            "descricao" to descricao,
            "imageString" to imageString,
            "dataCriacao" to System.currentTimeMillis()
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Postado com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Fecha a tela e volta para a Home
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
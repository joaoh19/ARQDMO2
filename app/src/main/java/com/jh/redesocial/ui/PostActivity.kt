package com.jh.redesocial.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.jh.redesocial.databinding.ActivityPostBinding
import com.jh.redesocial.util.Base64Converter
import com.jh.redesocial.util.LocalizadorHelper

class PostActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPostBinding.inflate(layoutInflater) }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var cidadeDetectada: String = ""

    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.imgPostPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Tenta obter a cidade assim que abre a tela
        obterLocalizacao()

        binding.btnSelecionarImagem.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnPublicar.setOnClickListener {
            publicarPost()
        }
    }

    private fun obterLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        val helper = LocalizadorHelper(this)
        helper.obterLocalizacaoAtual(object : LocalizadorHelper.Callback {
            override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double) {
                cidadeDetectada = endereco.locality ?: endereco.subAdminArea ?: ""
                if (cidadeDetectada.isNotEmpty()) {
                    Toast.makeText(this@PostActivity, "Localizado em: $cidadeDetectada", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onErro(mensagem: String) {
                cidadeDetectada = ""
            }
        })
    }

    private fun publicarPost() {
        val descricao = binding.edtDescricaoPost.text.toString()

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Escreva uma legenda!", Toast.LENGTH_SHORT).show()
            return
        }

        val imageString = Base64Converter.drawableToString(binding.imgPostPreview.drawable)

        // Criando o objeto post com o campo "cidade"
        val post = hashMapOf(
            "descricao" to descricao,
            "imageString" to imageString,
            "cidade" to cidadeDetectada, // IMPORTANTE: Para o filtro da Home funcionar
            "data" to Timestamp.now()
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Postado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
package com.jh.redesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jh.redesocial.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    // Lógica da imagem (conforme perfil1.png)
    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.profilePicture.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnAlterarFoto.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSalvarPerfil.setOnClickListener {
            salvarDadosFirestore()
        }
    }

    private fun salvarDadosFirestore() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val email = currentUser.email.toString()
            val username = binding.username.text.toString()
            val nomeCompleto = binding.nomeCompleto.text.toString()

            // Simulação do Base64Converter que aparece no seu print
            // Se você não tiver essa classe, precisará remover ou criar
            val fotoPerfilString = "" // Base64Converter.drawableToString(binding.profilePicture.drawable)

            val dados = hashMapOf(
                "nomeCompleto" to nomeCompleto,
                "username" to username,
                "fotoPerfil" to fotoPerfilString
            )

            db.collection("usuarios").document(email)
                .set(dados)
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
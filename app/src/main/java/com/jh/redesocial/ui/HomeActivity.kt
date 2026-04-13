package com.jh.redesocial.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jh.redesocial.adapter.PostAdapter
import com.jh.redesocial.databinding.ActivityHomeBinding
import com.jh.redesocial.model.Post
import com.jh.redesocial.util.Base64Converter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var posts = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarPerfil()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Botão Carregar Feed
        binding.btnCarregarFeed.setOnClickListener {
            carregarPosts()
        }

        // NOVO: Botão para abrir tela de postagem
        binding.btnNovoPost.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }

        // NOVO: Clique na foto para editar perfil
        binding.imgLogo.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarPerfil() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        val imageString = document.data!!["fotoPerfil"]?.toString() ?: ""
                        if (imageString.isNotEmpty()) {
                            val bitmap = Base64Converter.stringToBitmap(imageString)
                            binding.imgLogo.setImageBitmap(bitmap)
                        }
                        binding.txtUsername.text = document.data!!["username"].toString()
                        binding.txtNomeCompleto.text = document.data!!["nomeCompleto"].toString()
                    }
                }
            }
    }

    private fun carregarPosts() {
        val db = FirebaseFirestore.getInstance()

        // Adicionado orderBy para ver os posts mais novos primeiro (opcional)
        db.collection("posts")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    posts.clear()
                    for (document in task.result.documents) {
                        val descricao = document.data!!["descricao"].toString()
                        val imageString = document.data!!["imageString"].toString()

                        val bitmap = Base64Converter.stringToBitmap(imageString)
                        posts.add(Post(descricao, bitmap))
                    }

                    val adapter = PostAdapter(posts.toTypedArray())
                    binding.recyclerView.adapter = adapter
                }
            }
    }

    // Dica: Atualiza os dados do perfil quando voltar da ProfileActivity
    override fun onResume() {
        super.onResume()
        configurarPerfil()
    }
}
package com.jh.redesocial.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jh.redesocial.adapter.PostAdapter
import com.jh.redesocial.databinding.ActivityHomeBinding
import com.jh.redesocial.model.Post
import com.jh.redesocial.util.Base64Converter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    // Atributo privado para armazenar a lista de posts [cite: 50]
    private var posts = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser?.email.toString()

        val db = Firebase.firestore

        // --- Lógica existente: Carregar dados do usuário ---
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

        // --- Nova Lógica: Configurar o Feed (RecyclerView) ---

        // 1. Configura o Gerenciador de Layout 
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // 2. Listener do botão para carregar os posts [cite: 44, 48]
        binding.btnCarregarFeed.setOnClickListener {
            carregarPosts()
        }
    }

    private fun carregarPosts() {
        val db = Firebase.firestore

        // Busca todos os documentos na coleção "posts" [cite: 48, 53]
        db.collection("posts").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    posts.clear() // Limpa a lista antes de carregar novos [cite: 49]

                    for (document in task.result.documents) { [cite: 58]
                        // Extrai os dados conforme as chaves determinadas no PDF [cite: 60, 61, 70]
                        val descricao = document.data!!["descricao"].toString() [cite: 61]
                        val imageString = document.data!!["imageString"].toString() [cite: 60]

                        // Converte a string Base64 para Bitmap [cite: 61]
                        val bitmap = Base64Converter.stringToBitmap(imageString)

                        // Adiciona o novo objeto Post na lista [cite: 49, 61]
                        posts.add(Post(descricao, bitmap))
                    }

                    // 3. Configura o Adapter com os posts carregados [cite: 65, 67, 69]
                    val adapter = PostAdapter(posts.toTypedArray())
                    binding.recyclerView.adapter = adapter
                }
            }
    }
}
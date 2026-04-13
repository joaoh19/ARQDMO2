package com.jh.redesocial.ui

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

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.btnCarregarFeed.setOnClickListener {
            carregarPosts()
        }
    }

    private fun carregarPosts() {
        val db = FirebaseFirestore.getInstance()

        db.collection("posts").get()
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
}
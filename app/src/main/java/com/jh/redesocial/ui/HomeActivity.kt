package com.jh.redesocial.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jh.redesocial.adapter.PostAdapter
import com.jh.redesocial.databinding.ActivityHomeBinding
import com.jh.redesocial.model.Post
import com.jh.redesocial.util.Base64Converter
import com.jh.redesocial.util.LocalizadorHelper

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var posts = ArrayList<Post>()
    private val LOCATION_PERMISSION_CODE = 1001
    private var filtroCidade: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarPerfil()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Configuração de Busca
        binding.btnBuscar.setOnClickListener { executarBusca() }
        binding.btnLimparBusca.setOnClickListener { limparBusca() }
        binding.edtBuscarCidade.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                executarBusca()
                true
            } else false
        }

        binding.btnCarregarFeed.setOnClickListener {
            filtroCidade = null
            carregarPosts()
        }

        binding.btnNovoPost.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }

        binding.imgLogo.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        carregarPosts()
    }

    private fun executarBusca() {
        val cidade = binding.edtBuscarCidade.text.toString().trim()
        if (cidade.isEmpty()) {
            limparBusca()
            return
        }
        filtroCidade = cidade
        binding.btnLimparBusca.visibility = View.VISIBLE
        carregarPosts(cidade)
    }

    private fun limparBusca() {
        filtroCidade = null
        binding.edtBuscarCidade.setText("")
        binding.btnLimparBusca.visibility = View.GONE
        carregarPosts()
    }

    private fun carregarPosts(cidade: String? = null) {
        val db = FirebaseFirestore.getInstance()
        var query: Query = db.collection("posts")

        if (cidade != null) {
            query = query.whereEqualTo("cidade", cidade)
        }

        // Ordenação por data (opcional, requer índice no Firestore)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                posts.clear()
                for (document in task.result.documents) {
                    val descricao = document.data?.get("descricao")?.toString() ?: ""
                    val imageString = document.data?.get("imageString")?.toString() ?: ""
                    val cidadePost = document.data?.get("cidade")?.toString() ?: ""

                    val bitmap = if (imageString.isNotEmpty()) Base64Converter.stringToBitmap(imageString) else null

                    // Se o seu modelo Post aceitar cidade, adicione aqui
                    posts.add(Post(descricao, bitmap))
                }

                val adapter = PostAdapter(posts.toTypedArray())
                binding.recyclerView.adapter = adapter

                if (posts.isEmpty() && cidade != null) {
                    Toast.makeText(this, "Nenhum post em $cidade", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun configurarPerfil() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser?.email ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios").document(email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.exists()) {
                    val document = task.result
                    val imageString = document.data?.get("fotoPerfil")?.toString() ?: ""
                    if (imageString.isNotEmpty()) {
                        binding.imgLogo.setImageBitmap(Base64Converter.stringToBitmap(imageString))
                    }
                    binding.txtUsername.text = document.data?.get("username")?.toString()
                    binding.txtNomeCompleto.text = document.data?.get("nomeCompleto")?.toString()
                }
            }
    }

    // Método para ser usado na PostActivity ou quando precisar da cidade atual
    private fun obterCidadeAtual(callback: (String?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
            callback(null)
            return
        }

        val helper = LocalizadorHelper(this)
        helper.obterLocalizacaoAtual(object : LocalizadorHelper.Callback {
            override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double) {
                val cidade = endereco.locality ?: endereco.subAdminArea ?: "Desconhecida"
                callback(cidade)
            }
            override fun onErro(mensagem: String) {
                Toast.makeText(this@HomeActivity, mensagem, Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        configurarPerfil()
    }
}
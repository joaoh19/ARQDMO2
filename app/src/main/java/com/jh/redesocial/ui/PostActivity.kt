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
import com.jh.redesocial.util.LocalizadorHelper // Certifique-se que o pacote está correto

class PostActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPostBinding.inflate(layoutInflater) }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var cidadeDetectada: String = ""
    private val LOCATION_PERMISSION_CODE = 1001 // Baseado no exemplo do PDF [cite: 351]

    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.imgPostPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Clique para adicionar localização de forma ativa
        binding.btnAdicionarLocalizacao.setOnClickListener {
            solicitarLocalizacao()
        }

        binding.btnSelecionarImagem.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnPublicar.setOnClickListener {
            publicarPost()
        }
    }

    private fun solicitarLocalizacao() {
        // Verifica permissões em tempo de execução [cite: 320, 360, 444]
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
            return
        }

        // Feedback visual de carregamento
        binding.txtLocalizacaoPost.text = "Buscando localização..."

        // Instancia o Helper baseado no seu arquivo de exemplo
        val helper = LocalizadorHelper(this)
        helper.obterLocalizacaoAtual(object : LocalizadorHelper.Callback {
            override fun onLocalizacaoRecebida(endereco: Address, latitude: Double, longitude: Double) {
                // Atualização obrigatória na Thread Principal (UI Thread) [cite: 448, 459]
                runOnUiThread {
                    // Extrai cidade e estado conforme lógica de endereço [cite: 573, 577]
                    val cidade = endereco.locality ?: ""
                    val estado = endereco.adminArea ?: ""

                    cidadeDetectada = if (cidade.isNotEmpty()) "$cidade, $estado" else estado

                    // Atualiza o texto do botão com o local encontrado
                    binding.txtLocalizacaoPost.text = cidadeDetectada
                    binding.txtLocalizacaoPost.setTextColor(android.graphics.Color.BLACK)
                }
            }

            override fun onErro(mensagem: String) {
                runOnUiThread {
                    binding.txtLocalizacaoPost.text = "Toque para tentar novamente"
                    Toast.makeText(this@PostActivity, "Erro: $mensagem", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // Gerencia o resultado do pedido de permissão [cite: 389, 462]
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            solicitarLocalizacao()
        }
    }

    private fun publicarPost() {
        val descricao = binding.edtDescricaoPost.text.toString()

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Escreva uma legenda!", Toast.LENGTH_SHORT).show()
            return
        }

        val imageString = Base64Converter.drawableToString(binding.imgPostPreview.drawable)

        // Cria o objeto para o Firestore incluindo o campo "cidade"
        val post = hashMapOf(
            "descricao" to descricao,
            "imageString" to imageString,
            "cidade" to cidadeDetectada,
            "data" to Timestamp.now()
        )

        db.collection("posts").add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Postado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao publicar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
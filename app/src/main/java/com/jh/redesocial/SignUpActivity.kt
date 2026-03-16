package com.jh.redesocial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jh.redesocial.HomeActivity
import com.jh.redesocial.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFirebase()
        setupListeners()
    }

    private fun setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupListeners() {
        binding.btnCriarConta.setOnClickListener { criarConta() }
    }

    private fun criarConta() {
        val email = binding.edtEmailSignUp.text.toString()
        val password = binding.edtPasswordSignUp.text.toString()
        val confirmPassword = binding.edtConfirmPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                firebaseAuth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Redireciona para Home após o cadastro com sucesso
                            startActivity(Intent(this, ProfileActivity::class.java))
                            finish()
                        } else {
                            // Exibe o erro vindo do Firebase (ex: email mal formatado ou já existente)
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }
}
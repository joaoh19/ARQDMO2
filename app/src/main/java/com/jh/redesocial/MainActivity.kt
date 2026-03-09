package com.jh.redesocial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.jh.redesocial.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFireBase()
        setupListeners()

        }

        fun setupFireBase(){
            firebaseAuth = FirebaseAuth.getInstance()
        }

        fun setupListeners(){
            binding.btnLogar.setOnClickListener{(autenticarUsuario())}
            }

                fun autenticarUsuario(){

                    val email = binding.edtEmail.text.toString()
                    val password = binding.edtPassword.text.toString()

                    firebaseAuth
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Erro no login", Toast.LENGTH_LONG).show()
                            }
                        }

                    }
            }




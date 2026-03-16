package com.jh.redesocial

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser!!.email.toString()
        val db = Firebase.firestore
        db.collection("usuarios").document(email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val document = task.result
                    val imageString = document.data!!["fotoPerfil"].toString()
                    val bitmap = Base64Converter.stringToBitmap(imageString)
                    binding.imgLogo.setImageBitmap(bitmap)
                    binding.txtUsername.text = document.data!!["username"].toString()
                    binding.txtNomeCompleto.text = document.data!!["nomeCompleto"].toString()


        }
    }

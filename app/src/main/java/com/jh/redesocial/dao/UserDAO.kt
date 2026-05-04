package com.jh.redesocial.dao

import com.google.firebase.firestore.FirebaseFirestore
import com.jh.redesocial.model.User // Certifique-se de ter a data class User

class UserDAO {
    private val db = FirebaseFirestore.getInstance()

    fun buscarPerfil(email: String, callback: (Map<String, Any>?) -> Unit) {
        db.collection("usuarios").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    callback(document.data)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}
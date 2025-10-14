package com.example.appmusicayletras.Opciones_Login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmusicayletras.MainActivity
import com.example.appmusicayletras.R
import com.example.appmusicayletras.Registro_email
import com.example.appmusicayletras.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth
//import com.google.android.gms.auth.api.signin.GoogleSignInClient


class Login_email : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

   // private lateinit var googleSignInClient: GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        //comprobarSesion()


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.BtnIngresar.setOnClickListener {
            validarInfo()
        }

        binding.TxtRegistrarme.setOnClickListener {
            startActivity(Intent(this@Login_email, Registro_email::class.java))
        }
    }

    private var email = ""
    private var password = ""

    private fun validarInfo() {
        email = binding.EtEmail.text.toString().trim()
        password = binding.EtPassword.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmail.error = "email invalido"
            binding.EtEmail.requestFocus()
        }
        else if(email.isEmpty()){
            binding.EtEmail.error = "Ingrese un email"
            binding.EtEmail.requestFocus()
        }
        else if(password.isEmpty()){
            binding.EtPassword.error = "Ingrese el password"
            binding.EtPassword.requestFocus()
        }
        else{
            loginUsuario()
        }

    }

    private fun loginUsuario() {
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
                Toast.makeText(this,
                    "Bienvenido",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this,
                    "No se pudo ingresar debido a: ${e.message}",
                    Toast.LENGTH_SHORT).show()

            }

    }
}
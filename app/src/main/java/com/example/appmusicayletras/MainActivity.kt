package com.example.appmusicayletras

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appmusicayletras.Fragmentos.FragmentBuscar
import com.example.appmusicayletras.Fragmentos.FragmentInicio
import com.example.appmusicayletras.Fragmentos.FragmentMiMusica
import com.example.appmusicayletras.Fragmentos.FragmentPerfil
import com.example.appmusicayletras.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()
        VerFragmentInicio()


        binding.BottomNV.setOnItemSelectedListener { Item ->
            when (Item.itemId) {
                R.id.Item_Inicio->{
                    VerFragmentInicio()
                    true
                }
                R.id.Item_buscar->{
                    VerFragmentBuscar()
                    true
                }
                R.id.Item_perfil->{
                    VerFragmentPerfil()
                    true
                }
                R.id.Item_Mi_musica->{
                    VerFragmentMiMusica()
                    true
                }
                else->{
                    false
                }
            }
        }


    }

    private fun comprobarSesion(){
        if(firebaseAuth.currentUser == null){
            startActivity(Intent(this, OpcionesLogin::class.java))
            finishAffinity()
        }
    }
    private fun VerFragmentInicio(){
        binding.TituloRL.setText("Inicio")
        val fragment = FragmentInicio()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentInicio").commit()
        //fragmentTransition.commit()
    }
    private fun VerFragmentBuscar(){
        binding.TituloRL.setText("Buscar")
        val fragment = FragmentBuscar()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentBuscar").commit()
        //fragmentTransition.commit()

    }
    private fun VerFragmentMiMusica(){
        binding.TituloRL.setText("Chats")
        val fragment = FragmentMiMusica()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentMiMusica").commit()
        //fragmentTransition.commit()

    }
    private fun VerFragmentPerfil(){
        binding.TituloRL.setText("Chats")
        val fragment = FragmentPerfil()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentPerfil").commit()
        //fragmentTransition.commit()

    }


}
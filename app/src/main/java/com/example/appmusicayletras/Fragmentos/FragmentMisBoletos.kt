package com.example.appmusicayletras.Fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appmusicayletras.adaptadores.AdaptadorBoletos
import com.example.appmusicayletras.databinding.FragmentMisBoletosBinding
import com.example.appmusicayletras.modelos.Boleto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentMisBoletos : Fragment() {

    private lateinit var binding: FragmentMisBoletosBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var adaptador: AdaptadorBoletos
    private val listaBoletos = ArrayList<Boleto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMisBoletosBinding.inflate(inflater, container, false)
        binding.recyclerBoletos.layoutManager = LinearLayoutManager(requireContext())
        adaptador = AdaptadorBoletos(requireContext(), listaBoletos)
        binding.recyclerBoletos.adapter = adaptador

        dbRef = FirebaseDatabase.getInstance().getReference("Boletos")

        obtenerBoletosUsuario()

        return binding.root
    }

    private fun obtenerBoletosUsuario() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaBoletos.clear()
                for (boletoSnap in snapshot.children) {
                    val boleto = boletoSnap.getValue(Boleto::class.java)
                    if (boleto != null && boleto.usuarioId == userId) {
                        listaBoletos.add(boleto)
                    }
                }
                adaptador.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar boletos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

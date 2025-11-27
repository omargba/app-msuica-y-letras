package com.example.appmusicayletras.Fragmentos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusicayletras.R
import com.example.appmusicayletras.adaptadores.AdaptadorNoticias
import com.example.appmusicayletras.modelos.Noticia
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class FragmentBuscar : Fragment() {

    private lateinit var etBuscar: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoResultados: TextView
    private lateinit var noticiasOriginales: ArrayList<Noticia>
    private lateinit var noticiasFiltradas: ArrayList<Noticia>
    private lateinit var adaptador: AdaptadorNoticias

    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_buscar, container, false)

        etBuscar = view.findViewById(R.id.EtBuscar)
        tvNoResultados = view.findViewById(R.id.TvNoResultados)
        recyclerView = view.findViewById(R.id.RVResultados)

        recyclerView.layoutManager = LinearLayoutManager(context)

        noticiasOriginales = arrayListOf()
        noticiasFiltradas = arrayListOf()

        adaptador = AdaptadorNoticias(requireContext(), noticiasFiltradas)
        recyclerView.adapter = adaptador

        dbRef = FirebaseDatabase.getInstance().getReference("Noticias")

        cargarEventos()

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filtrar(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    private fun cargarEventos() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                noticiasOriginales.clear()

                for (snap in snapshot.children) {
                    val n = snap.getValue(Noticia::class.java)
                    if (n != null) noticiasOriginales.add(n)
                }

                filtrar(etBuscar.text.toString())
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun filtrar(texto: String) {
        noticiasFiltradas.clear()

        val t = texto.lowercase()

        for (evento in noticiasOriginales) {
            if (evento.titulo.lowercase().contains(t) ||
                evento.descripcion.lowercase().contains(t) ||
                evento.ciudad.lowercase().contains(t)
            ) {
                noticiasFiltradas.add(evento)
            }
        }

        // 🔥 MOSTRAR/OCULTAR mensaje de "No se encontraron resultados"
        if (noticiasFiltradas.isEmpty() && texto.isNotEmpty()) {
            tvNoResultados.visibility = View.VISIBLE
        } else {
            tvNoResultados.visibility = View.GONE
        }

        adaptador.notifyDataSetChanged()

        adaptador.notifyDataSetChanged()
    }
}

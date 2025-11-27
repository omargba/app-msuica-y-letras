package com.example.appmusicayletras.Fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusicayletras.adaptadores.AdaptadorNoticias
import com.example.appmusicayletras.modelos.Noticia
import com.google.firebase.database.*
import com.example.appmusicayletras.R

class FragmentInicio : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noticiasList: ArrayList<Noticia>
    private lateinit var noticiasFiltradas: ArrayList<Noticia>
    private lateinit var adaptadorNoticias: AdaptadorNoticias
    private lateinit var dbRef: DatabaseReference

    private lateinit var filtroCiudad: AutoCompleteTextView
    private lateinit var filtroCategoria: AutoCompleteTextView
    private lateinit var btnLimpiarFiltros: Button

    private var ciudades = ArrayList<String>()
    private var categorias = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        recyclerView = view.findViewById(R.id.recycler_noticia)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        filtroCiudad = view.findViewById(R.id.FiltroCiudad)
        filtroCategoria = view.findViewById(R.id.FiltroCategoria)
        btnLimpiarFiltros = view.findViewById(R.id.BtnLimpiarFiltros)   // 👈 AQUÍ

        noticiasList = arrayListOf()
        noticiasFiltradas = arrayListOf()

        adaptadorNoticias = AdaptadorNoticias(requireContext(), noticiasFiltradas)
        recyclerView.adapter = adaptadorNoticias

        obtenerDatosNoticias()
        configurarFiltros()

        // Click del botón limpiar
        btnLimpiarFiltros.setOnClickListener {
            limpiarFiltros()
        }

        return view
    }


    private fun obtenerDatosNoticias() {
        dbRef = FirebaseDatabase.getInstance().getReference("Noticias")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noticiasList.clear()
                ciudades.clear()
                categorias.clear()

                if (snapshot.exists()) {
                    for (noticiaSnap in snapshot.children) {

                        val noticia = noticiaSnap.getValue(Noticia::class.java)

                        noticia?.let {
                            noticiasList.add(it)

                            // Agregar ciudades y categorías únicas
                            if (it.ciudad.isNotEmpty() && !ciudades.contains(it.ciudad)) {
                                ciudades.add(it.ciudad)
                            }
                            if (it.categoria.isNotEmpty() && !categorias.contains(it.categoria)) {
                                categorias.add(it.categoria)
                            }
                        }
                    }

                    // Actualizar adaptadores de filtros
                    actualizarOpcionesFiltros()

                    aplicarFiltros()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun configurarFiltros() {

        filtroCiudad.setOnItemClickListener { _, _, _, _ ->
            aplicarFiltros()
        }

        filtroCategoria.setOnItemClickListener { _, _, _, _ ->
            aplicarFiltros()
        }
    }


    private fun actualizarOpcionesFiltros() {
        // Ordenar alfabeticamente
        ciudades.sort()
        categorias.sort()

        val adapterCiudad =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ciudades)
        val adapterCategoria = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categorias)

        filtroCiudad.setAdapter(adapterCiudad)
        filtroCategoria.setAdapter(adapterCategoria)
    }


    /** FILTRAR RESULTADOS **/
    private fun aplicarFiltros() {
        val ciudadSeleccionada = filtroCiudad.text.toString()
        val categoriaSeleccionada = filtroCategoria.text.toString()

        noticiasFiltradas.clear()

        for (evento in noticiasList) {

            val coincideCiudad =
                ciudadSeleccionada.isEmpty() || evento.ciudad == ciudadSeleccionada

            val coincideCategoria =
                categoriaSeleccionada.isEmpty() || evento.categoria == categoriaSeleccionada

            if (coincideCiudad && coincideCategoria) {
                noticiasFiltradas.add(evento)
            }
        }

        adaptadorNoticias.notifyDataSetChanged()
    }

    private fun limpiarFiltros() {
        // Limpia los AutoCompleteTextView
        filtroCiudad.setText("")
        filtroCategoria.setText("")

        // Aplica filtros de nuevo (esto deja la lista completa)
        aplicarFiltros()
    }


}

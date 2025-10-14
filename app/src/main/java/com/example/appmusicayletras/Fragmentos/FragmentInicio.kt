package com.example.appmusicayletras.Fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var adaptadorNoticias: AdaptadorNoticias
    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        recyclerView = view.findViewById(R.id.recycler_noticia)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        noticiasList = arrayListOf<Noticia>()
        adaptadorNoticias = AdaptadorNoticias(requireContext(), noticiasList)
        recyclerView.adapter = adaptadorNoticias

        obtenerDatosNoticias()

        return view
    }

    private fun obtenerDatosNoticias() {
        // Apuntamos al nodo "Noticias" en Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("Noticias")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpiamos la lista antes de agregar los nuevos datos
                noticiasList.clear()
                if (snapshot.exists()){
                    for (noticiaSnap in snapshot.children){
                        val noticiaData = noticiaSnap.getValue(Noticia::class.java)
                        noticiasList.add(noticiaData!!)
                    }
                    // Notificamos al adaptador que los datos han cambiado
                    adaptadorNoticias.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error si la lectura es cancelada
            }
        })
    }
}
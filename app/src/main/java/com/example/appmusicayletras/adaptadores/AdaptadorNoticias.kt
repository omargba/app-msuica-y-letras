package com.example.appmusicayletras.adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appmusicayletras.R

import com.example.appmusicayletras.modelos.Noticia

class AdaptadorNoticias(
    private val context: Context,
    private val noticiasList: ArrayList<Noticia>
) : RecyclerView.Adapter<AdaptadorNoticias.HolderNoticia>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderNoticia {
        val view = LayoutInflater.from(context).inflate(R.layout.item_noticias, parent, false)
        return HolderNoticia(view)
    }

    override fun onBindViewHolder(holder: HolderNoticia, position: Int) {
        val noticia = noticiasList[position]

        holder.titulo.text = noticia.titulo
        holder.descripcion.text = noticia.descripcion

        // Usar Glide para cargar la imagen
        Glide.with(context)
            .load(noticia.imagenUrl)
           // .placeholder(R.drawable.cargaaa) // Opcional: una imagen de carga
            .into(holder.imagen)
    }

    override fun getItemCount(): Int {
        return noticiasList.size
    }

    // Clase interna para manejar las vistas de cada item
    inner class HolderNoticia(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.iv_noticia)
        val titulo: TextView = itemView.findViewById(R.id.tv_titulo_noticia)
        val descripcion: TextView = itemView.findViewById(R.id.tv_descripcion_noticia)
    }
}
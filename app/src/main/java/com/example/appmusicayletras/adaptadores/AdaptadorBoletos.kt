package com.example.appmusicayletras.adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmusicayletras.DetalleBoletoActivity
import com.example.appmusicayletras.R
import com.example.appmusicayletras.modelos.Boleto

class AdaptadorBoletos(
    private val context: Context,
    private val listaBoletos: ArrayList<Boleto>
) : RecyclerView.Adapter<AdaptadorBoletos.BoletoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoletoViewHolder {
        val vista = LayoutInflater.from(context).inflate(R.layout.item_boleto, parent, false)
        return BoletoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: BoletoViewHolder, position: Int) {
        val boleto = listaBoletos[position]
        holder.tvEvento.text = boleto.evento
        holder.tvFecha.text = "Fecha: ${boleto.fechaEvento}"
        holder.tvMarca.text = "Pago con: ${boleto.marcaTarjeta} ••••${boleto.ultimos4}"

        when (boleto.marcaTarjeta.uppercase()) {
            "VISA" -> holder.ivMarca.setImageResource(R.drawable.ic_visa)
            "MASTERCARD" -> holder.ivMarca.setImageResource(R.drawable.ic_mastercard)
            else -> holder.ivMarca.setImageResource(R.drawable.ic_card_placeholder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetalleBoletoActivity::class.java)
            intent.putExtra("boletoId", boleto.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listaBoletos.size

    inner class BoletoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEvento: TextView = itemView.findViewById(R.id.tvEvento)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvMarca: TextView = itemView.findViewById(R.id.tvMarca)
        val ivMarca: ImageView = itemView.findViewById(R.id.ivMarca)
    }
}

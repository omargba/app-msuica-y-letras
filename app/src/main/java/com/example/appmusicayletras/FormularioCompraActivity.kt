package com.example.appmusicayletras

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appmusicayletras.databinding.ActivityFormularioCompraBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class FormularioCompraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioCompraBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val dbRef = FirebaseDatabase.getInstance().getReference("Boletos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioCompraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Detección de marca de tarjeta
        binding.etTarjeta.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val number = s.toString()
                val brand = detectCardBrand(number)
                when (brand) {
                    "VISA" -> binding.ivMarcaTarjeta.setImageResource(R.drawable.ic_visa)
                    "MASTERCARD" -> binding.ivMarcaTarjeta.setImageResource(R.drawable.ic_mastercard)
                    else -> binding.ivMarcaTarjeta.setImageResource(R.drawable.ic_card_placeholder)
                }
                binding.btnConfirmarCompra.isEnabled = number.length >= 15
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        binding.etFechaExp.addTextChangedListener(object : TextWatcher {
            private var isEditing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isEditing) return
                isEditing = true

                var input = s.toString().replace("/", "")
                if (input.length >= 3) {
                    input = input.substring(0, 2) + "/" + input.substring(2)
                }
                binding.etFechaExp.setText(input)
                binding.etFechaExp.setSelection(input.length.coerceAtMost(binding.etFechaExp.text.length))
                isEditing = false
            }
        })

        // 🔹 Listener del botón de compra
        binding.btnConfirmarCompra.setOnClickListener {
            guardarBoletoEnFirebase()
        }
    }


    private fun detectCardBrand(number: String): String {
        val n = number.filter { it.isDigit() }
        if (n.startsWith("4")) return "VISA"
        if (n.take(2).toIntOrNull() in 51..55) return "MASTERCARD"
        if (n.startsWith("34") || n.startsWith("37")) return "AMEX"
        return "DESCONOCIDA"
    }

    private fun guardarBoletoEnFirebase() {
        val idBoleto = UUID.randomUUID().toString()
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val direccion = binding.etDireccion.text.toString()
        val numeroTarjeta = binding.etTarjeta.text.toString()
        val marca = detectCardBrand(numeroTarjeta)
        val fechaExp = binding.etFechaExp.text.toString()

        if (nombre.isEmpty() || correo.isEmpty() || direccion.isEmpty() || numeroTarjeta.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val datosBoleto = mapOf(
            "id" to idBoleto,
            "usuarioId" to firebaseAuth.currentUser?.uid,
            "nombre" to nombre,
            "correo" to correo,
            "direccion" to direccion,
            "marcaTarjeta" to marca,
            "ultimos4" to numeroTarjeta.takeLast(4),
            "fechaExp" to fechaExp,
            "evento" to intent.getStringExtra("tituloEvento"),
            "fechaEvento" to intent.getStringExtra("fechaEvento"),
            "codigoQR" to idBoleto
        )

        dbRef.child(idBoleto).setValue(datosBoleto).addOnSuccessListener {
            mostrarDialogoQR(idBoleto)
        }.addOnFailureListener {
            Toast.makeText(this, "Error al guardar boleto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoQR(codigo: String) {
        val qrWriter = com.google.zxing.qrcode.QRCodeWriter()
        val bitMatrix = qrWriter.encode(codigo, com.google.zxing.BarcodeFormat.QR_CODE, 400, 400)
        val bmp = android.graphics.Bitmap.createBitmap(400, 400, android.graphics.Bitmap.Config.RGB_565)
        for (x in 0 until 400) {
            for (y in 0 until 400) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }

        val imageView = android.widget.ImageView(this)
        imageView.setImageBitmap(bmp)
        imageView.setPadding(30, 30, 30, 30)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Compra confirmada 🎟️")
            .setMessage("Tu boleto fue generado exitosamente.\nEscanea este código QR en el acceso al evento.")
            .setView(imageView)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                finish() // Cierra y vuelve al detalle del evento
            }
            .show()
    }


}

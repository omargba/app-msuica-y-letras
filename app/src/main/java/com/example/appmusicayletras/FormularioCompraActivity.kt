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

        // ------------------------------
        // 🔹 AUTORELLENO DEL USUARIO
        // ------------------------------
        FirebaseDatabase.getInstance().getReference("Usuarios")
            .child(firebaseAuth.uid!!)
            .get()
            .addOnSuccessListener { snapshot ->

                val nombre = snapshot.child("nombres").value?.toString() ?: ""
                val correo = firebaseAuth.currentUser?.email ?: ""
                val direccion = snapshot.child("direccion").value?.toString() ?: ""

                binding.etNombre.setText(nombre)
                binding.etCorreo.setText(correo)
                binding.etDireccion.setText(direccion)
            }

        // ------------------------------
        // 🔹 FORMATEO + MARCA DE TARJETA (UN SOLO LISTENER)
        // ------------------------------
        binding.etTarjeta.addTextChangedListener(object : TextWatcher {

            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                if (s == null) return

                isFormatting = true

                // Quitar espacios
                val digitsOnly = s.toString().replace(" ", "")

                // Limitar a 16 dígitos reales
                val limitedDigits = digitsOnly.take(16)

                // Formatear cada 4 números
                val formatted = StringBuilder()
                for (i in limitedDigits.indices) {
                    formatted.append(limitedDigits[i])
                    if ((i + 1) % 4 == 0 && (i + 1) != limitedDigits.length) {
                        formatted.append(" ")
                    }
                }

                s.replace(0, s.length, formatted.toString())

                // Detectar marca
                val brand = detectCardBrand(limitedDigits)
                when (brand) {
                    "VISA" -> binding.ivMarcaTarjeta.setImageResource(R.drawable.ic_visa)
                    "MASTERCARD" -> binding.ivMarcaTarjeta.setImageResource(R.drawable.ic_mastercard)
                    "AMEX" -> binding.ivMarcaTarjeta.setImageResource(R.drawable.ic_amex)
                    else -> binding.ivMarcaTarjeta.setImageResource(R.drawable.ic_card_placeholder)
                }

                // Activar botón solo si tiene 16 dígitos
                binding.btnConfirmarCompra.isEnabled = limitedDigits.length == 16

                isFormatting = false
            }
        })

        // ------------------------------
        // 🔹 FECHA DE EXPIRACIÓN MM/AA
        // ------------------------------
        binding.etFechaExp.addTextChangedListener(object : TextWatcher {

            private var editing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (editing) return
                editing = true

                var input = s.toString().replace("/", "")

                if (input.length >= 3) {
                    input = input.substring(0, 2) + "/" + input.substring(2)
                }

                binding.etFechaExp.setText(input)
                binding.etFechaExp.setSelection(input.length)

                editing = false
            }
        })

        // ------------------------------
        // 🔹 CONFIRMAR COMPRA
        // ------------------------------
        binding.btnConfirmarCompra.setOnClickListener {
            guardarBoletoEnFirebase()
        }
    }

    // -----------------------------------
    // 🔍 DETECTAR MARCA DE TARJETA
    // -----------------------------------
    private fun detectCardBrand(number: String): String {
        val n = number.filter { it.isDigit() }
        if (n.startsWith("4")) return "VISA"
        if (n.take(2).toIntOrNull() in 51..55) return "MASTERCARD"
        if (n.startsWith("34") || n.startsWith("37")) return "AMEX"
        return "DESCONOCIDA"
    }

    // -----------------------------------
    // 🔹 GUARDAR BOLETO EN FIREBASE
    // -----------------------------------
    private fun guardarBoletoEnFirebase() {
        val idBoleto = UUID.randomUUID().toString()

        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val direccion = binding.etDireccion.text.toString()
        val tarjeta = binding.etTarjeta.text.toString()
        val fechaExp = binding.etFechaExp.text.toString()
        val marca = detectCardBrand(tarjeta.replace(" ", ""))


        if (nombre.isEmpty() || correo.isEmpty() || direccion.isEmpty() || tarjeta.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val datosBoleto = mapOf(
            "id" to idBoleto,
            "usuarioId" to firebaseAuth.uid,
            "nombre" to nombre,
            "correo" to correo,
            "direccion" to direccion,
            "marcaTarjeta" to marca,
            "ultimos4" to tarjeta.takeLast(4),
            "fechaExp" to fechaExp,
            "evento" to intent.getStringExtra("tituloEvento"),
            "fechaEvento" to intent.getStringExtra("fechaEvento"),
            "codigoQR" to idBoleto,
            "imagenUrl" to intent.getStringExtra("imagenUrlEvento")
        )


        dbRef.child(idBoleto).setValue(datosBoleto)
            .addOnSuccessListener { mostrarDialogoQR(idBoleto) }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar boleto", Toast.LENGTH_SHORT).show()
            }
    }

    // -----------------------------------
    // 🔹 MOSTRAR QR GENERADO
    // -----------------------------------
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
            .setMessage("Tu boleto fue generado exitosamente.\nEscanea este código QR al entrar.")
            .setView(imageView)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }
}

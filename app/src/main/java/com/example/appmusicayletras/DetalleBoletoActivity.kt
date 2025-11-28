package com.example.appmusicayletras

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appmusicayletras.databinding.ActivityDetalleBoletoBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class DetalleBoletoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleBoletoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleBoletoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val boletoId = intent.getStringExtra("boletoId") ?: return

        binding.root.alpha = 0f
        binding.root.animate().alpha(1f).setDuration(300).start()

        binding.btnDescargarPDF.setOnClickListener {
            generarPDF()
        }


        // Cargar boleto
        FirebaseDatabase.getInstance().getReference("Boletos")
            .child(boletoId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {

                    val evento = snapshot.child("evento").value.toString()
                    val fecha = snapshot.child("fechaEvento").value.toString()
                    val marca = snapshot.child("marcaTarjeta").value.toString()
                    val ultimos4 = snapshot.child("ultimos4").value.toString()
                    val codigoQR = snapshot.child("codigoQR").value.toString()

                    binding.tvEventoBoleto.text = evento
                    binding.tvFechaBoleto.text = "Fecha: $fecha"
                    binding.tvPagoBoleto.text = "Pago con: $marca ••••$ultimos4"

                    // 🔥 NUEVO: cargar imagen desde el nodo Noticias
                    cargarImagenDesdeNoticias(evento)

                    generarQR(codigoQR)
                }
            }
    }

    private fun cargarImagenDesdeNoticias(nombreEvento: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Noticias")

        ref.get().addOnSuccessListener { snapshot ->
            for (n in snapshot.children) {
                val titulo = n.child("titulo").value.toString()
                if (titulo == nombreEvento) {
                    val imagenUrl = n.child("imagenUrl").value.toString()

                    Glide.with(this)
                        .load(imagenUrl)
                        .into(binding.ivImagenEvento)
                }
            }
        }
    }

    private fun generarQR(text: String) {
        val qrWriter = QRCodeWriter()
        val bitMatrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500)
        val bmp = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565)

        for (x in 0 until 500) {
            for (y in 0 until 500) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        binding.ivQRBoleto.setImageBitmap(bmp)
    }

    private fun generarPDF() {
        try {
            val nombreArchivo = "Boleto_${binding.tvEventoBoleto.text}.pdf"

            // 📂 Guardar en DESCARGAS visible para el usuario
            val carpetaDescargas = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )

            val archivo = java.io.File(carpetaDescargas, nombreArchivo)
            val outputStream = java.io.FileOutputStream(archivo)

            val document = android.graphics.pdf.PdfDocument()

            // Convertir layout a imagen
            binding.root.measure(
                View.MeasureSpec.makeMeasureSpec(binding.root.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            binding.root.layout(0, 0, binding.root.measuredWidth, binding.root.measuredHeight)

            val bitmap = Bitmap.createBitmap(
                binding.root.measuredWidth,
                binding.root.measuredHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            binding.root.draw(canvas)

            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(
                bitmap.width,
                bitmap.height,
                1
            ).create()

            val page = document.startPage(pageInfo)
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)

            document.writeTo(outputStream)
            document.close()

            Toast.makeText(this, "PDF guardado en Descargas ✔", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


}


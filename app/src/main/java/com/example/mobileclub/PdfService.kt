package com.example.mobileclub

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Environment
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.VerticalAlignment
import java.io.ByteArrayOutputStream
import java.io.File
import android.Manifest
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table

class PdfService(private val context: Context) {

    // Colores consistentes con tu diseño
    private val colorFondoCarnet = DeviceRgb(255, 255, 255) // Fondo blanco semi-transparente
    private val colorTextoNegro = DeviceRgb(0, 0, 0)
    private val colorBorde = DeviceRgb(0, 0, 300) // Borde semi-transparente
    private val colorTitulo = DeviceRgb(33, 33, 33) // Gris oscuro para el título

    fun generarCarnetPDF(nombre: String, dni: String, fecha: String, rutaPersonalizada: String? = null): String {
        try {
            // Verificar permisos y crear directorio (código existente...)

            if (!isExternalStorageWritable()) {
                throw Exception("No hay permisos de escritura en el almacenamiento")
            }

            val pdfDir = when {
                // Si se proporciona una ruta personalizada
                !rutaPersonalizada.isNullOrEmpty() -> File(rutaPersonalizada)

                // Por defecto: almacenamiento público de Descargas
                else -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            }

            // Crear directorio si no existe
            if (!pdfDir.exists()) {
                pdfDir.mkdirs()
            }
            // Crear archivo PDF
            val fileName = "carnet_${dni}_${fecha.replace("/", "-")}.pdf"
            val filePath = File(pdfDir, fileName)

            // Inicializar PDF con tamaño A6 horizontal
            val writer = PdfWriter(filePath)
            val pdf = PdfDocument(writer)
            val document = Document(pdf, PageSize.A6.rotate())

            // Configurar márgenes más pequeños para mejor aprovechamiento del espacio
            document.setMargins(15f, 15f, 15f, 15f)

            try {
                // Crear contenedor principal (similar a tu CardView)
                val mainContainer = Div()
                    .setBackgroundColor(colorFondoCarnet)
                    .setPadding(16f)
                    .setMargin(0f)

                // Cabecera con logo y título
                val header = Table(floatArrayOf(1f, 3f))
                    .useAllAvailableWidth()
                    .setBorder(null)

                // Agregar logo
                val logo = AppCompatResources.getDrawable(context, R.drawable.logo_mi_club_1)
                if (logo != null) {
                    val bitmap = (logo as BitmapDrawable).bitmap
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val logoImage = Image(ImageDataFactory.create(stream.toByteArray()))
                        .setWidth(48f)
                        .setHeight(48f)

                    header.addCell(
                        Cell().add(logoImage)
                            .setBorder(null)
                            .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    )
                }

                // Título "MI CLUB"
                header.addCell(
                    Cell().add(
                        Paragraph("MI CLUB")
                            .setFontSize(20f)
                            .setBold()
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontColor(colorTitulo)
                    )
                        .setBorder(null)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                )

                mainContainer.add(header)

                // Datos del socio
                val datosContainer = Div()
                    .setMarginTop(10f)

                // Nombre con estilo bold y tamaño más grande
                datosContainer.add(
                    Paragraph(nombre)
                        .setFontSize(18f)
                        .setBold()
                        .setFontColor(colorTextoNegro)
                )

                // DNI
                datosContainer.add(
                    Paragraph("DNI: $dni")
                        .setFontSize(16f)
                        .setMarginTop(8f)
                        .setFontColor(colorTextoNegro)
                )

                // Fecha
                datosContainer.add(
                    Paragraph("Fecha: $fecha")
                        .setFontSize(14f)
                        .setMarginTop(8f)
                        .setFontColor(colorTextoNegro)
                )

                mainContainer.add(datosContainer)

                // Agregar el contenedor principal al documento
                document.add(mainContainer)

                document.close()
                return filePath.absolutePath

            } catch (e: Exception) {
                document.close()
                throw e
            }

        } catch (e: Exception) {
            throw Exception("Error al generar PDF del carnet: ${e.message}", e)
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    // Función auxiliar para obtener diferentes rutas de almacenamiento
    fun getRutasDisponibles(): Map<String, String> {
        return mapOf(
            "Documentos App" to context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath + "/carnets",
            "Descargas" to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,
            "Documentos" to Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/MiClub/Carnets",
            "Almacenamiento interno" to context.filesDir.absolutePath + "/carnets"
        )
    }
}
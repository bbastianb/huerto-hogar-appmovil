package com.abs.huerto_hogar_appmovil.data.remote.dto

import com.abs.huerto_hogar_appmovil.R
import com.abs.huerto_hogar_appmovil.data.model.Producto

data class ProductoDto(
    val id: String,
    val nombre: String,
    val precio: Double,
    val unidad:String,
    val stock:String,
    val descripcion: String,
    val img:String?

)

fun ProductoDto.toEntity(): Producto {
    val categoria = when {
        id.startsWith("FR") -> "Frutas"
        id.startsWith("VR") -> "Verduras"
        id.startsWith("PO") -> "Orgánicos"
        else -> "Otros"
    }

    // unidad: "x kilo", "x frasco de 500gr" → nos quedamos con lo que vaya después de la "x"
    val medidaLimpia = unidad
        .removePrefix("x")
        .trim()

    // stock: "150 kilos" → 150
    val stockInt = stock.filter { it.isDigit() }.toIntOrNull() ?: 0

    return Producto(
        id = id,
        nombre = nombre,
        precio = precio,
        categoria = categoria,
        stock = stockInt,
        descripcion = descripcion,
        imagen = mapImagenLocal(img,id),
        medida = medidaLimpia
    )
}
private fun mapImagenLocal(img: String?, id: String): Int {
    // si usas el nombre de img que guardaste en la BD
    return when (img) {
        "manzanas"  -> R.drawable.manzanas
        "platanos"  -> R.drawable.platanos
        "naranja"   -> R.drawable.naranja
        "frutillas" -> R.drawable.frutillas
        "kiwi"      -> R.drawable.kiwi
        "miel"      -> R.drawable.miel5
        "zanahoria" -> R.drawable.zanahoria
        "espinaca"  -> R.drawable.espinaca
        "pimenton"  -> R.drawable.pimenton
        "limon"     -> R.drawable.limon
        "cebolla"   -> R.drawable.cebolla
        else -> when (id) {        // fallback por id
            "FR001" -> R.drawable.manzanas
            "FR002" -> R.drawable.platanos
            "FR003" -> R.drawable.naranja
            "FR004" -> R.drawable.frutillas
            "FR005" -> R.drawable.kiwi
            "PO001" -> R.drawable.miel5
            "VR001" -> R.drawable.zanahoria
            "VR002" -> R.drawable.espinaca
            "VR003" -> R.drawable.pimenton
            "VR004" -> R.drawable.limon
            "VR005" -> R.drawable.cebolla
            else    -> R.drawable.manzanas
        }
    }
}
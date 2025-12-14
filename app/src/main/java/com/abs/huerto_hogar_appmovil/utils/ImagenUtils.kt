package com.abs.huerto_hogar_appmovil.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun ByteArray.toBitmap(): Bitmap? =
    BitmapFactory.decodeByteArray(this, 0, this.size)

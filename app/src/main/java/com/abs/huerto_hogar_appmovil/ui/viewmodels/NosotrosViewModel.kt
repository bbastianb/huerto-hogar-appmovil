package com.abs.huerto_hogar_appmovil.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.abs.huerto_hogar_appmovil.data.model.Sucursal
import com.abs.huerto_hogar_appmovil.data.repository.SucursalRepository


class NosotrosViewModel : ViewModel() {
    val sucursales: List<Sucursal> = SucursalRepository.obtenerSucursales()
}

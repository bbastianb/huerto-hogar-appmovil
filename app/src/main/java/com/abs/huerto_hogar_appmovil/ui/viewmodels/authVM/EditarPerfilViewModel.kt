package com.abs.huerto_hogar_appmovil.ui.viewmodels.authVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditarPerfilUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val fono: String = "",
    val direccion: String = "",
    val comuna: String = "",
    val region: String = "",
    val mensaje: String? = null,
    val error: String? = null,
    val isSaving: Boolean = false
)

class EditarPerfilViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarPerfilUiState())
    val uiState: StateFlow<EditarPerfilUiState> = _uiState

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        val usuario = usuarioRepository.obtenerUsuarioActual()

        if (usuario == null) {
            _uiState.update {
                it.copy(error = "No hay usuario logueado, vuelva a iniciar sesión.")
            }
        } else {
            _uiState.update {
                it.copy(
                    nombre = usuario.nombre,
                    apellido = usuario.apellido,
                    email = usuario.email,
                    fono = if (usuario.telefono.isBlank()) "" else usuario.telefono,
                    direccion = usuario.direccion,
                    comuna = usuario.comuna,
                    region = usuario.region,
                    error = null,
                    mensaje = null
                )
            }
        }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, mensaje = null, error = null) }
    }

    fun onApellidoChange(value: String) {
        _uiState.update { it.copy(apellido = value, mensaje = null, error = null) }
    }

    fun onFonoChange(value: String) {
        _uiState.update { it.copy(fono = value, mensaje = null, error = null) }
    }

    fun onDireccionChange(value: String) {
        _uiState.update { it.copy(direccion = value, mensaje = null, error = null) }
    }

    fun onComunaChange(value: String) {
        _uiState.update { it.copy(comuna = value, mensaje = null, error = null) }
    }

    fun onRegionChange(value: String) {
        _uiState.update { it.copy(region = value, mensaje = null, error = null) }
    }

    fun guardarCambios() {
        val state = _uiState.value
        val usuarioActual = usuarioRepository.obtenerUsuarioActual()

        if (usuarioActual == null) {
            _uiState.update { it.copy(error = "No hay usuario en sesión") }
            return
        }

        if (state.nombre.isBlank() || state.apellido.isBlank()) {
            _uiState.update { it.copy(error = "Nombre y apellido son campos obligatorios.") }
            return
        }

        val fonoLimpio = state.fono.filter { it.isDigit() }
        val fonoInt = if (fonoLimpio.isBlank())
            0
        else fonoLimpio.toIntOrNull() ?: 0

        val actualizado = usuarioActual.copy(
            nombre = state.nombre.trim(),
            apellido = state.apellido.trim(),
            telefono = fonoInt.toString(),
            direccion = state.direccion.trim(),
            comuna = state.comuna.trim(),
            region = state.region.trim()
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, mensaje = null) }

            try {
                val resultado = usuarioRepository.actualizarPerfilUsuario(actualizado)

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        nombre = resultado.nombre,
                        apellido = resultado.apellido,
                        email = resultado.email,
                        fono = if (resultado.telefono.isBlank()) "" else resultado.telefono,
                        direccion = resultado.direccion,
                        comuna = resultado.comuna,
                        region = resultado.region,
                        mensaje = "Perfil actualizado correctamente"
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Error al guardar: ${e.message ?: "desconocido"}"
                    )
                }
            }
        }
    }
}

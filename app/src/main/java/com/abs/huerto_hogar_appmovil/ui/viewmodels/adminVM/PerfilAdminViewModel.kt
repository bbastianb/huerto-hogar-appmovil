package com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class PerfilAdminUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val fono: String = "",
    val direccion: String = "",
    val comuna: String = "",
    val region: String = "",
    val nuevaContrasenna: String = "",
    val confirmarContrasenna: String = "",
    val mensaje: String? = null,
    val error: String? = null,
    val isSaving: Boolean = false
)

class PerfilAdminViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilAdminUiState())
    val uiState: StateFlow<PerfilAdminUiState> = _uiState

    private var nuevaFotoFile: File? = null

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        val usuario = usuarioRepository.obtenerUsuarioActual()

        if (usuario == null) {
            _uiState.update {
                it.copy(error = "No hay admin en sesión, vuelve a iniciar sesión.")
            }
        } else {
            _uiState.update {
                it.copy(
                    nombre = usuario.nombre,
                    apellido = usuario.apellido,
                    email = usuario.email,
                    fono = usuario.telefono,
                    direccion = usuario.direccion,
                    comuna = usuario.comuna,
                    region = usuario.region,
                    mensaje = null,
                    error = null
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

    fun onNuevaContrasennaChange(value: String) {
        _uiState.update { it.copy(nuevaContrasenna = value, mensaje = null, error = null) }
    }

    fun onConfirmarContrasennaChange(value: String) {
        _uiState.update { it.copy(confirmarContrasenna = value, mensaje = null, error = null) }
    }

    fun onNuevaFotoSeleccionada(file: File?) {
        nuevaFotoFile = file
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensaje = null, error = null) }
    }

    fun guardarCambios() {
        val state = _uiState.value
        val usuarioBase = usuarioRepository.obtenerUsuarioActual()

        if (usuarioBase == null) {
            _uiState.update { it.copy(error = "No hay admin en sesión") }
            return
        }

        if (state.nombre.isBlank() || state.apellido.isBlank()) {
            _uiState.update { it.copy(error = "Nombre y apellido son obligatorios.") }
            return
        }

        var contrasennaNuevaFinal: String? = null
        if (state.nuevaContrasenna.isNotBlank() || state.confirmarContrasenna.isNotBlank()) {
            if (state.nuevaContrasenna != state.confirmarContrasenna) {
                _uiState.update { it.copy(error = "Las contraseñas no coinciden.") }
                return
            }
            if (state.nuevaContrasenna.length < 8) {
                _uiState.update { it.copy(error = "La nueva contraseña debe tener al menos 8 caracteres.") }
                return
            }
            contrasennaNuevaFinal = state.nuevaContrasenna
        }

        val usuarioActualizado = usuarioBase.copy(
            nombre = state.nombre.trim(),
            apellido = state.apellido.trim(),
            telefono = state.fono.trim(),
            direccion = state.direccion.trim(),
            comuna = state.comuna.trim(),
            region = state.region.trim(),
            contrasenna = contrasennaNuevaFinal
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, mensaje = null) }

            try {
                val usuarioServidor = usuarioRepository.actualizarPerfilUsuario(usuarioActualizado)

                nuevaFotoFile?.let { file ->
                    usuarioRepository.actualizarFotoPerfilActual(file)
                    nuevaFotoFile = null
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        nombre = usuarioServidor.nombre,
                        apellido = usuarioServidor.apellido,
                        email = usuarioServidor.email,
                        fono = usuarioServidor.telefono,
                        direccion = usuarioServidor.direccion,
                        comuna = usuarioServidor.comuna,
                        region = usuarioServidor.region,
                        nuevaContrasenna = "",
                        confirmarContrasenna = "",
                        mensaje = "Perfil actualizado correctamente"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Error al guardar cambios: ${e.message ?: "desconocido"}"
                    )
                }
            }
        }
    }
}

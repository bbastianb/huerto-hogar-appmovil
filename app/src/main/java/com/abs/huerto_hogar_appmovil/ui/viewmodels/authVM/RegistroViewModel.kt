package com.abs.huerto_hogar_appmovil.ui.viewmodels.authVM

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Usuario
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _fotoUri = MutableStateFlow<Uri?>(null)
    val fotoUri: StateFlow<Uri?> = _fotoUri.asStateFlow()

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _apellido = MutableStateFlow("")
    val apellido: StateFlow<String> = _apellido.asStateFlow()

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo.asStateFlow()

    private val _contrasenna = MutableStateFlow("")
    val contrasenna: StateFlow<String> = _contrasenna.asStateFlow()

    private val _confirmarContrasenna = MutableStateFlow("")
    val confirmarContrasenna: StateFlow<String> = _confirmarContrasenna.asStateFlow()

    private val _fono = MutableStateFlow("")
    val fono: StateFlow<String> = _fono.asStateFlow()

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion.asStateFlow()

    private val _comuna = MutableStateFlow("")
    val comuna: StateFlow<String> = _comuna.asStateFlow()

    private val _region = MutableStateFlow("")
    val region: StateFlow<String> = _region.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()


    fun onFotoCapturada(uri: Uri) {
        _fotoUri.value = uri
    }

    fun onNombreChange(nuevoNombre: String) {
        _nombre.value = nuevoNombre
        _errorMessage.value = null
    }

    fun onApellidoChange(nuevoApellido: String) {
        _apellido.value = nuevoApellido
        _errorMessage.value = null
    }

    fun onCorreoChange(nuevoCorreo: String) {
        _correo.value = nuevoCorreo
        _errorMessage.value = null
    }

    fun onContrasennaChange(nuevaContrasenna: String) {
        _contrasenna.value = nuevaContrasenna
        _errorMessage.value = null
    }

    fun onConfirmarContrasennaChange(nuevaConfirmacion: String) {
        _confirmarContrasenna.value = nuevaConfirmacion
        _errorMessage.value = null
    }

    fun onFonoChange(nuevoFono: String) {
        _fono.value = nuevoFono.filter { it.isDigit() }
        _errorMessage.value = null
    }

    fun onDireccionChange(nuevaDireccion: String) {
        _direccion.value = nuevaDireccion
        _errorMessage.value = null
    }

    fun onComunaChange(nuevaComuna: String) {
        _comuna.value = nuevaComuna
        _errorMessage.value = null
    }

    fun onRegionChange(nuevaRegion: String) {
        _region.value = nuevaRegion
        _errorMessage.value = null
    }

    private fun isValidCorreo(correo: String): Boolean {
        val correoLowerCase = correo.trim().lowercase()

        return correoLowerCase.endsWith("@duoc.cl") ||
                correoLowerCase.endsWith("@profesor.duoc.cl") ||
                correoLowerCase.endsWith("@gmail.com")
    }

    fun limpiarFormulario() {
        _nombre.value = ""
        _apellido.value = ""
        _correo.value = ""
        _contrasenna.value = ""
        _confirmarContrasenna.value = ""
        _fono.value = ""
        _direccion.value = ""
        _comuna.value = ""
        _region.value = ""
        _fotoUri.value = null
        _errorMessage.value = null
    }

    fun resetSuccess() {
        _isSuccess.value = false
    }
    fun registrar() {

        // Validaciones

        if (_nombre.value.isBlank()) {
            _errorMessage.value = "El nombre es obligatorio"
            return
        }

        if (!_nombre.value.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$"))) {
            _errorMessage.value = "El nombre solo puede contener letras y espacios"
            return
        }

        if (_apellido.value.isBlank()) {
            _errorMessage.value = "El apellido es obligatorio"
            return
        }

        if (!_apellido.value.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$"))) {
            _errorMessage.value = "El apellido solo puede contener letras y espacios"
            return
        }

        if (_correo.value.isBlank()) {
            _errorMessage.value = "El correo es obligatorio"
            return
        }

        if (_correo.value.length > 100) {
            _errorMessage.value = "El correo no puede tener más de 100 caracteres"
            return
        }

        if (!isValidCorreo(_correo.value)) {
            _errorMessage.value = "Solo se permiten correos @duoc.cl, @profesor.duoc.cl y @gmail.com"
            return
        }

        if (_contrasenna.value.isBlank()) {
            _errorMessage.value = "La contraseña es obligatoria"
            return
        }

        if (_contrasenna.value.length < 8 ) {
            _errorMessage.value = "La contraseña debe tener al menos 8 caracteres"
            return
        }

        if (_contrasenna.value != _confirmarContrasenna.value) {
            _errorMessage.value = "Las contraseñas no coinciden"
            return
        }

        if (_fono.value.isBlank()) {
            _errorMessage.value = "El teléfono es obligatorio"
            return
        }

        if (_fono.value.length != 9) {
            _errorMessage.value = "El teléfono debe tener 9 dígitos"
            return
        }

        if (_direccion.value.isBlank()) {
            _errorMessage.value = "La dirección es obligatoria"
            return
        }

        if (_comuna.value.isBlank()) {
            _errorMessage.value = "La comuna es obligatoria"
            return
        }

        if (_region.value.isBlank()) {
            _errorMessage.value = "La región es obligatoria"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val usuario = Usuario(
                    nombre = _nombre.value.trim(),
                    apellido = _apellido.value.trim(),
                    email = _correo.value.trim().lowercase(),
                    contrasenna = _contrasenna.value,
                    telefono = _fono.value.trim(),
                    direccion = _direccion.value.trim(),
                    comuna = _comuna.value.trim(),
                    region = _region.value.trim(),
                    rol = "usuario"
                )

                val correcto = usuarioRepository.registrar(usuario)

                if (correcto) {
                    _isSuccess.value = true
                } else {
                    val errorBack = usuarioRepository.obtenerUltimoErrorRegistro()
                    _errorMessage.value = errorBack ?: "El correo ya está registrado o hubo un error en el servidor"                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al registrar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

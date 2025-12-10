package com.abs.huerto_hogar_appmovil.ui.viewmodels.authVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Usuario
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _contrasenna = MutableStateFlow("")
    val contrasenna: StateFlow<String> = _contrasenna.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _usuarioLogueado = MutableStateFlow<Usuario?>(null)
    val usuarioLogueado: StateFlow<Usuario?> = _usuarioLogueado.asStateFlow()

    private val _loginExitosoUsuario = MutableStateFlow(false)
    val loginExitosoUsuario: StateFlow<Boolean> = _loginExitosoUsuario.asStateFlow()

    private val _loginExitosoAdmin = MutableStateFlow(false)
    val loginExitosoAdmin: StateFlow<Boolean> = _loginExitosoAdmin.asStateFlow()


    fun onEmailChange(nuevoEmail: String) {
        _email.value = nuevoEmail
        _errorMessage.value = null
    }

    fun onContrasennaChange(nuevaContrasenna: String) {
        _contrasenna.value = nuevaContrasenna
        _errorMessage.value = null
    }

    fun limpiarCampos() {
        _email.value = ""
        _contrasenna.value = ""
        _errorMessage.value = null
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = false
        _loginExitosoAdmin.value = false
        _loginExitosoUsuario.value = false
    }

    fun login() {
        // Validaciones básicas
        if (_email.value.isBlank()) {
            _errorMessage.value = "El correo es obligatorio"
            return
        }

        if (_contrasenna.value.isBlank()) {
            _errorMessage.value = "La contraseña es obligatoria"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                delay(1000)

                val usuario = usuarioRepository.login(
                    email = _email.value.trim().lowercase(),
                    contrasenna = _contrasenna.value
                )

                if (usuario != null) {
                    _usuarioLogueado.value = usuario
                    _loginSuccess.value = true

                    when (usuario.rol) {
                        "admin" -> {
                            _loginExitosoAdmin.value = true
                        }
                        "usuario" -> {
                            _loginExitosoUsuario.value = true
                        }
                        else -> {
                            _errorMessage.value = "Rol de usuario no válido"
                        }
                    }

                    limpiarCampos()
                } else {
                    _errorMessage.value = "Correo o contraseña incorrectos"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al iniciar sesión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

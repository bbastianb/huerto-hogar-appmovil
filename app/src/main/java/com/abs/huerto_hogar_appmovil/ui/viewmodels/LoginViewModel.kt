package com.abs.huerto_hogar_appmovil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Usuario
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class LoginViewModel(private val usuarioRepository: UsuarioRepository): ViewModel() {

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo.asStateFlow()

    private val _contrasenna = MutableStateFlow("")
    val contrasenna : StateFlow<String> = _contrasenna.asStateFlow()

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

    fun onCorreoChange(nuevoCorreo: String) {
        _correo.value = nuevoCorreo
        _errorMessage.value = null
    }

    fun onContrasennaChange(nuevaContrasenna: String) {
        _contrasenna.value = nuevaContrasenna
        _errorMessage.value = null
    }


    fun limpiarCampos() {
        _correo.value = ""
        _contrasenna.value = ""
        _errorMessage.value = null
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }

    fun login (){
        if (_correo.value.isBlank()){
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
                delay(2000) //Tiene el tiempo de la carga

                val usuario = usuarioRepository.login(
                    correo = _correo.value.trim().lowercase(),
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
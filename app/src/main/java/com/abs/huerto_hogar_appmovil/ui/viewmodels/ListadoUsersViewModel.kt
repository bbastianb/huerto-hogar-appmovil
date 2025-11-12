package com.abs.huerto_hogar_appmovil.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Usuario
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListadoUsersViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _mostrarDialogo = MutableStateFlow(false)
    val mostrarDialogo: StateFlow<Boolean> = _mostrarDialogo.asStateFlow()

    private val _usuarioParaEliminar = MutableStateFlow<Usuario?>(null)
    val usuarioParaEliminar: StateFlow<Usuario?> = _usuarioParaEliminar.asStateFlow()

    fun abrirDialogo(usuario: Usuario) {
        _usuarioParaEliminar.value = usuario
        _mostrarDialogo.value = true
    }

    fun cerrarDialogo() {
        _mostrarDialogo.value = false
        _usuarioParaEliminar.value = null
    }

    fun confirmarEliminacion() {
        val usuario = _usuarioParaEliminar.value
        if (usuario != null) {
            eliminarUsuario(usuario.correo)
        }
        cerrarDialogo()
    }

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val listaUsuarios = repository.obtenerTodosLosUsuarios()
                _usuarios.value = listaUsuarios
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar usuarios: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarUsuario(correo: String) {
        viewModelScope.launch {
            try {
                val resultado = repository.eliminarUsuario(correo)
                if (resultado) {
                    cargarUsuarios()
                } else {
                    _errorMessage.value = "No se pudo eliminar el usuario"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
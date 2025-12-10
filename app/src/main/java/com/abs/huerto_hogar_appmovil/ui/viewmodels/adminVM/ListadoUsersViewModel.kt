package com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.huerto_hogar_appmovil.data.model.Usuario
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListadoUsersViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _mostrarDialogo = MutableStateFlow(false)
    val mostrarDialogo: StateFlow<Boolean> = _mostrarDialogo.asStateFlow()

    private val _usuarioParaEliminar = MutableStateFlow<Usuario?>(null)
    val usuarioParaEliminar: StateFlow<Usuario?> = _usuarioParaEliminar.asStateFlow()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _ordenarPor = MutableStateFlow("Nombre")
    val ordenarPor: StateFlow<String> = _ordenarPor.asStateFlow()

    private var usuariosCompletos: List<Usuario> = emptyList()

    init {
        cargarUsuarios()
    }

    fun cargarUsuarios() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val listaUsuarios = repository.obtenerUsuariosAdmin()
                usuariosCompletos = listaUsuarios
                aplicarFiltrosYOrdenamiento()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar usuarios: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarTextoBusqueda(texto: String) {
        _textoBusqueda.value = texto
        aplicarFiltrosYOrdenamiento()
    }

    fun actualizarOrdenamiento(orden: String) {
        _ordenarPor.value = orden
        aplicarFiltrosYOrdenamiento()
    }

    fun limpiarFiltros() {
        _textoBusqueda.value = ""
        _ordenarPor.value = "Nombre"
        aplicarFiltrosYOrdenamiento()
    }

    private fun aplicarFiltrosYOrdenamiento() {
        var usuariosFiltrados = usuariosCompletos

        // FILTRO POR TEXTO
        if (_textoBusqueda.value.isNotBlank()) {
            val textoBusquedaLower = _textoBusqueda.value.lowercase()

            usuariosFiltrados = usuariosFiltrados.filter { usuario ->
                usuario.nombre.lowercase().contains(textoBusquedaLower) ||
                        usuario.apellido.lowercase().contains(textoBusquedaLower) ||
                        usuario.email.lowercase().contains(textoBusquedaLower) ||
                        usuario.region.lowercase().contains(textoBusquedaLower) ||
                        usuario.comuna.lowercase().contains(textoBusquedaLower) ||
                        usuario.direccion.lowercase().contains(textoBusquedaLower) ||
                        "${usuario.nombre} ${usuario.apellido}".lowercase().contains(textoBusquedaLower)
            }
        }

        // ORDENAMIENTO
        usuariosFiltrados = when (_ordenarPor.value) {
            "Nombre" -> usuariosFiltrados.sortedBy { it.nombre }
            "Email" -> usuariosFiltrados.sortedBy { it.email }
            "Región" -> usuariosFiltrados.sortedBy { it.region }
            "Comuna" -> usuariosFiltrados.sortedBy { it.comuna }
            else -> usuariosFiltrados
        }

        _usuarios.value = usuariosFiltrados
    }

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
            eliminarUsuario(usuario.id)
        }
        cerrarDialogo()
    }

    fun eliminarUsuario(id: Long) {
        viewModelScope.launch {
            try {
                val resultado = repository.eliminarUsuarioAdmin(id)
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

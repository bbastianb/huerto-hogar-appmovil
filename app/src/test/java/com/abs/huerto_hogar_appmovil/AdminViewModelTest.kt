package com.abs.huerto_hogar_appmovil


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import com.abs.huerto_hogar_appmovil.data.repository.PedidoRepository
import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto
import com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM.AdminViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {

    @Test
    fun `cuando backend devuelve ordenes se actualiza uiState`() = runTest {

        // 🔹 SETUP dispatcher de prueba
        Dispatchers.setMain(UnconfinedTestDispatcher())

        try {
            val repo = mockk<PedidoRepository>()

            coEvery { repo.obtenerOrdenesDesdeBackend() } returns listOf(
                OrdenResponseDto(1, "2025-12-13", 5000.0),
                OrdenResponseDto(2, "2025-12-13", 12000.0),
                OrdenResponseDto(3, "2025-12-12", 7990.0)
            )

            val vm = AdminViewModel(repo)

            advanceUntilIdle()

            val state = vm.uiState.value

            assertEquals(3, state.totalOrdenes)
            assertEquals(3, state.ordenesBackend.size)
            assertFalse(state.isLoading)
            assertNull(state.error)

        } finally {
            // 🔹 LIMPIEZA
            Dispatchers.resetMain()
        }
    }
}


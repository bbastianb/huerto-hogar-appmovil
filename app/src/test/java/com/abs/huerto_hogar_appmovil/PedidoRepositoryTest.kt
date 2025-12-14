package com.abs.huerto_hogar_appmovil

import com.abs.huerto_hogar_appmovil.data.local.dao.PedidoDao
import com.abs.huerto_hogar_appmovil.data.remote.api.OrdenApi
import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto
import com.abs.huerto_hogar_appmovil.data.repository.PedidoRepository
import com.abs.huerto_hogar_appmovil.data.repository.UsuarioRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PedidoRepositoryTest {

    @Test
    fun `obtenerOrdenesDesdeBackend retorna lista cuando api responde ok`() = runTest {

        // ✅ Simular usuario autenticado
        UsuarioRepository.setTokenForTests ("fake-token")

        val api = mockk<OrdenApi>()
        val dao = mockk<PedidoDao>(relaxed = true)

        coEvery { api.listarOrdenes(any()) } returns Response.success(
            listOf(
                OrdenResponseDto(1, "2025-12-13", 5000.0),
                OrdenResponseDto(2, "2025-12-14", 7000.0)
            )
        )

        val repo = PedidoRepository(dao, api)

        val result = repo.obtenerOrdenesDesdeBackend()

        assertEquals(2, result.size)
        assertEquals(1L, result[0].idOrden)
    }
}

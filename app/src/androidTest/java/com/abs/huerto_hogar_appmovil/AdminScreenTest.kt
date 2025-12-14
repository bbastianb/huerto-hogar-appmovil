package com.abs.huerto_hogar_appmovil

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import com.abs.huerto_hogar_appmovil.data.remote.dto.OrdenResponseDto
import com.abs.huerto_hogar_appmovil.ui.screens.adminScreens.AdminContent
import com.abs.huerto_hogar_appmovil.ui.viewmodels.adminVM.AdminUiState
import org.junit.Rule
import org.junit.Test

class AdminScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun adminContent_muestra_titulo_y_ordenes() {

        val state = AdminUiState(
            totalOrdenes = 3,
            ordenesBackend = listOf(
                OrdenResponseDto(1, "2025-12-13", 5000.0),
                OrdenResponseDto(2, "2025-12-14", 7000.0)
            ),
            isLoading = false,
            error = null
        )

        composeRule.setContent {
            AdminContent(
                state = state,
                onBack = {}
            )
        }

        composeRule.waitForIdle()


        composeRule.onNodeWithText("Panel administrador").assertIsDisplayed()
        composeRule.onNodeWithText("Ordenes totales").assertIsDisplayed()
        composeRule.onNodeWithText("Últimas ordenes").assertIsDisplayed()


        composeRule.onNodeWithText("Orden #1").assertIsDisplayed()
        composeRule.onNodeWithText("Total: $5000.0").assertIsDisplayed()
    }
}

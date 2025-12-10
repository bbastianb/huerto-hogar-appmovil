package com.abs.huerto_hogar_appmovil.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.abs.huerto_hogar_appmovil.data.model.RegionOP

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoRegionDropdown(
    regionCodigoSeleccionado: String?,
    onRegionCodigoChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val regionActual = RegionOP.values()
        .find { it.codigo == regionCodigoSeleccionado }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = regionActual?.label ?: "Selecciona una región",
            onValueChange = {},
            label = { Text("Región") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            RegionOP.values().forEach { region ->
                DropdownMenuItem(
                    text = { Text(region.label) },
                    onClick = {
                        onRegionCodigoChange(region.codigo)
                        expanded = false
                    }
                )
            }
        }
    }
}

package com.abs.huerto_hogar_appmovil.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abs.huerto_hogar_appmovil.ui.theme.GreenPrimary
import com.abs.huerto_hogar_appmovil.R
import com.abs.huerto_hogar_appmovil.ui.theme.GreenTertiary

@Composable
fun LoginScreen(
    onLoginOk: () -> Unit = {},
    onRegistroClick : () -> Unit = {}
    ) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        ){
        Image(
            painter = painterResource(id = R.drawable.logo_huerto),
            contentDescription = "Logo HH",
            modifier = Modifier.size(200.dp)
        )

    }

    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Text(
            "Bienvenido",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Icono de email") },
                singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Icono de contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(), // Para ocultar la contraseña
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLoginOk,
            colors = ButtonDefaults.buttonColors(GreenPrimary),
            modifier = Modifier.height(50.dp).fillMaxWidth()
        ) {
            Text(
                "Ingresar",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            "¿No tienes cuenta? Regístrate",
            color = GreenTertiary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable{ onRegistroClick() }
        )

    }

}


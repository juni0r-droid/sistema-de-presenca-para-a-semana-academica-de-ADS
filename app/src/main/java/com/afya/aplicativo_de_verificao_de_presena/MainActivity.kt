package com.afya.aplicativo_de_verificao_de_presena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AplicativodeVerificação_de_PresençaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicativodeVerificação_de_PresençaTheme {
                AfyaEventosApp()
            }
        }
    }
}

@Composable
fun AfyaEventosApp() {
    var autenticado by rememberSaveable { mutableStateOf(false) }
    if (autenticado) {
        TelaPrincipal(aoSair = { autenticado = false })
    } else {
        RotaLogin(aoLogar = { autenticado = true })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    AplicativodeVerificação_de_PresençaTheme {
        AfyaEventosApp()
    }
}

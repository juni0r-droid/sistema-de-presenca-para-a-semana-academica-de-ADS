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
    var usuarioLogado by rememberSaveable { mutableStateOf<DadosUsuario?>(null) }
    var mostrarCadastro by rememberSaveable { mutableStateOf(false) }

    if (usuarioLogado != null) {
        TelaPrincipal(
            nomeUsuario = usuarioLogado?.nome ?: "",
            tipoUsuario = usuarioLogado?.tipo ?: TipoUsuario.ALUNO,
            aoSair = { usuarioLogado = null }
        )
    } else if (mostrarCadastro) {
        RotaCadastro(
            aoCadastrar = { tipo, nome ->
                mostrarCadastro = false
                usuarioLogado = DadosUsuario(nome, tipo)
            },
            aoVoltar = { mostrarCadastro = false }
        )
    } else {
        RotaLogin(
            aoLogar = { tipo, nome ->
                usuarioLogado = DadosUsuario(nome, tipo)
            },
            aoIrParaCadastro = { mostrarCadastro = true }
        )
    }
}

data class DadosUsuario(
    val nome: String,
    val tipo: TipoUsuario
) : java.io.Serializable

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    AplicativodeVerificação_de_PresençaTheme {
        AfyaEventosApp()
    }
}

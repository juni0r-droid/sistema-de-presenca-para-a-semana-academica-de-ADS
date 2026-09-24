package com.afya.aplicativo_de_verificao_de_presena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    var abaSelecionada by rememberSaveable { mutableStateOf("inicio") }

    // Estados de validação e edição
    var eventoSendoValidado by remember { mutableStateOf<Evento?>(null) }
    var eventoSendoEditado by remember { mutableStateOf<Evento?>(null) }
    var mostrarPopupSucesso by remember { mutableStateOf(false) }
    var mostrarPopupErro by remember { mutableStateOf(false) }

    if (usuarioLogado != null) {
        val dados = usuarioLogado!!
        when (abaSelecionada) {
            "inicio" -> {
                RotaPrincipal(
                    usuario = dados,
                    aoSair = { usuarioLogado = null },
                    aoTrocarAba = { abaSelecionada = it },
                    aoCancelarInscricao = { evento ->
                        // Lógica de cancelamento visual ou chamada à API
                    },
                    aoIniciarValidacao = { evento ->
                        eventoSendoValidado = evento
                        abaSelecionada = "leitor_qr"
                    },
                    aoCriarEvento = {
                        eventoSendoEditado = null
                        abaSelecionada = "criar_evento"
                    },
                    aoEditarEvento = { evento ->
                        eventoSendoEditado = evento
                        abaSelecionada = "criar_evento"
                    },
                    aoExcluirEvento = { evento ->
                        // Lógica para excluir o evento
                    }
                )
            }
            "criar_evento" -> {
                RotaCriarEvento(
                    eventoParaEditar = eventoSendoEditado,
                    aoCriar = { novoEvento ->
                        abaSelecionada = "inicio"
                        eventoSendoEditado = null
                    },
                    aoVoltar = {
                        abaSelecionada = "inicio"
                        eventoSendoEditado = null
                    }
                )
            }
            "eventos" -> {
                RotaNovosEventos(
                    usuario = dados,
                    aoInscrever = { evento ->
                        // Ação ao inscrever no evento
                    },
                    aoTrocarAba = { abaSelecionada = it },
                    aoEditarEvento = { evento ->
                        eventoSendoEditado = evento
                        abaSelecionada = "criar_evento"
                    },
                    aoExcluirEvento = { evento ->
                        // Ação ao excluir evento
                    }
                )
            }
            "perfil" -> {
                TelaPerfil(
                    dadosUsuario = dados,
                    aoSair = { usuarioLogado = null },
                    aoTrocarAba = { novaAba -> abaSelecionada = novaAba }
                )
            }
            "leitor_qr" -> {
                RotaLeitorQR(
                    usuario = dados,
                    eventoId = eventoSendoValidado?.id ?: "",
                    chaveAcessoEvento = eventoSendoValidado?.chaveAcesso ?: "",
                    aoValidarSucesso = {
                        mostrarPopupSucesso = true
                        abaSelecionada = "inicio"
                        eventoSendoValidado = null
                    },
                    aoValidarErro = {
                        mostrarPopupErro = true
                        abaSelecionada = "inicio"
                        eventoSendoValidado = null
                    },
                    aoCancelar = {
                        abaSelecionada = "inicio"
                        eventoSendoValidado = null
                    }
                )
            }
            else -> {
                RotaPrincipal(
                    usuario = dados,
                    aoSair = { usuarioLogado = null },
                    aoTrocarAba = { abaSelecionada = it },
                    aoCancelarInscricao = { },
                    aoIniciarValidacao = { evento ->
                        eventoSendoValidado = evento
                        abaSelecionada = "leitor_qr"
                    }
                )
            }
        }
    } else if (mostrarCadastro) {
        RotaCadastro(
            aoCadastrar = { dados ->
                mostrarCadastro = false
                usuarioLogado = dados
            },
            aoVoltar = { mostrarCadastro = false }
        )
    } else {
        RotaLogin(
            aoLogar = { dados ->
                usuarioLogado = dados
            },
            aoIrParaCadastro = { mostrarCadastro = true }
        )
    }

    // Pop-ups de Validação
    if (mostrarPopupSucesso) {
        AlertDialog(
            onDismissRequest = { mostrarPopupSucesso = false },
            confirmButton = {
                Button(onClick = { mostrarPopupSucesso = false }) {
                    Text("OK")
                }
            },
            title = { Text("Sucesso", color = Color.Black) },
            text = { Text("Validação concluída com sucesso!", color = Color.DarkGray) },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.DarkGray,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarPopupErro) {
        AlertDialog(
            onDismissRequest = { mostrarPopupErro = false },
            confirmButton = {
                Button(
                    onClick = { mostrarPopupErro = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Tentar novamente", color = Color.White)
                }
            },
            title = { Text("Erro na Validação", color = Color.Black) },
            text = { Text("Não foi possível validar sua presença, tente novamente.", color = Color.DarkGray) },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.DarkGray,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    AplicativodeVerificação_de_PresençaTheme {
        AfyaEventosApp()
    }
}
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
    
    // Estados de validação
    var eventoSendoValidado by remember { mutableStateOf<Evento?>(null) }
    var eventoSendoEditado by remember { mutableStateOf<Evento?>(null) }
    var mostrarPopupSucesso by remember { mutableStateOf(false) }
    var mostrarPopupErro by remember { mutableStateOf(false) }

    // Estado compartilhado de eventos
    var eventosDisponiveis by remember {
        mutableStateOf(
            listOf(
                Evento(titulo = "Congresso de Medicina 2026", local = "Centro de Convenções", data = "10/10/2026", descricao = "Um mergulho nas novas tecnologias médicas e práticas clínicas modernas."),
                Evento(titulo = "Workshop de Programação Kotlin", local = "Laboratório 04", data = "15/10/2026", descricao = "Aprenda as melhores práticas de desenvolvimento Android com especialistas."),
                Evento(titulo = "Palestra: Carreira na Saúde", local = "Auditório B", data = "20/10/2026", descricao = "Insights valiosos sobre o mercado de trabalho e gestão de carreira.")
            )
        )
    }

    var eventosInscritos by remember {
        mutableStateOf(
            listOf(
                Evento(titulo = "Jornada Acadêmica 2026", local = "Auditório Central", data = "Hoje", descricao = "Evento de abertura do semestre acadêmico.")
            )
        )
    }

    if (usuarioLogado != null) {
        val dados = usuarioLogado!!
        when (abaSelecionada) {
            "inicio" -> {
                val listaParaExibir = if (dados.tipo == TipoUsuario.COORDENADOR) {
                    eventosDisponiveis.takeLast(5).reversed()
                } else {
                    eventosInscritos
                }
                
                TelaPrincipal(
                    nomeUsuario = dados.nome,
                    tipoUsuario = dados.tipo,
                    eventosInscritos = listaParaExibir,
                    aoSair = { usuarioLogado = null },
                    aoTrocarAba = { abaSelecionada = it },
                    aoCancelarInscricao = { evento ->
                        eventosInscritos = eventosInscritos - evento
                        eventosDisponiveis = eventosDisponiveis + evento
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
                        eventosDisponiveis = eventosDisponiveis.filter { it.id != evento.id }
                        eventosInscritos = eventosInscritos.filter { it.id != evento.id }
                    }
                )
            }
            "criar_evento" -> {
                TelaCriarEvento(
                    eventoParaEditar = eventoSendoEditado,
                    aoCriar = { novoEvento ->
                        if (eventoSendoEditado != null) {
                            // Atualiza evento existente
                            eventosDisponiveis = eventosDisponiveis.map {
                                if (it.id == novoEvento.id) novoEvento else it
                            }
                            eventosInscritos = eventosInscritos.map {
                                if (it.id == novoEvento.id) novoEvento else it
                            }
                        } else {
                            // Adiciona novo
                            eventosDisponiveis = eventosDisponiveis + novoEvento
                        }
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
                TelaNovosEventos(
                    tipoUsuario = dados.tipo,
                    eventosDisponiveis = if (dados.tipo == TipoUsuario.COORDENADOR) eventosDisponiveis + eventosInscritos else eventosDisponiveis,
                    aoInscrever = { evento ->
                        eventosInscritos = eventosInscritos + evento
                        eventosDisponiveis = eventosDisponiveis.filter { it.id != evento.id }
                    },
                    aoTrocarAba = { abaSelecionada = it },
                    aoEditarEvento = { evento ->
                        eventoSendoEditado = evento
                        abaSelecionada = "criar_evento"
                    },
                    aoExcluirEvento = { evento ->
                        eventosDisponiveis = eventosDisponiveis.filter { it.id != evento.id }
                        eventosInscritos = eventosInscritos.filter { it.id != evento.id }
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
                TelaLeitorQR(
                    aoValidarSucesso = {
                        eventoSendoValidado?.let { evento ->
                            eventosInscritos = eventosInscritos.map {
                                if (it == evento) it.copy(validado = true) else it
                            }
                        }
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
                TelaPrincipal(
                    nomeUsuario = dados.nome,
                    tipoUsuario = dados.tipo,
                    eventosInscritos = eventosInscritos,
                    aoSair = { usuarioLogado = null },
                    aoTrocarAba = { abaSelecionada = it },
                    aoCancelarInscricao = { evento ->
                        eventosInscritos = eventosInscritos - evento
                        eventosDisponiveis = eventosDisponiveis + evento
                    },
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
            title = { Text("Sucesso") },
            text = { Text("Validação concluída com sucesso!") },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarPopupErro) {
        AlertDialog(
            onDismissRequest = { mostrarPopupErro = false },
            confirmButton = {
                Button(onClick = { mostrarPopupErro = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Tentar novamente", color = Color.White)
                }
            },
            title = { Text("Erro na Validação") },
            text = { Text("Não foi possível validar sua presença, tente novamente.") },
            containerColor = Color.White,
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

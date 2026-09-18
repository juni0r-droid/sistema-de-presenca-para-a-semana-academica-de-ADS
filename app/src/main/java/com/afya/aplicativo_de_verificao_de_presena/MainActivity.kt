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
    var abaSelecionada by rememberSaveable { mutableStateOf("inicio") }

    // Estado compartilhado de eventos
    var eventosDisponiveis by remember {
        mutableStateOf(
            listOf(
                Evento("Congresso de Medicina 2026", "Centro de Convenções", "10/10/2026", "Um mergulho nas novas tecnologias médicas e práticas clínicas modernas."),
                Evento("Workshop de Programação Kotlin", "Laboratório 04", "15/10/2026", "Aprenda as melhores práticas de desenvolvimento Android com especialistas."),
                Evento("Palestra: Carreira na Saúde", "Auditório B", "20/10/2026", "Insights valiosos sobre o mercado de trabalho e gestão de carreira.")
            )
        )
    }

    var eventosInscritos by remember {
        mutableStateOf(
            listOf(
                Evento("Jornada Acadêmica 2026", "Auditório Central", "Hoje", "Evento de abertura do semestre acadêmico.")
            )
        )
    }

    if (usuarioLogado != null) {
        when (abaSelecionada) {
            "inicio" -> {
                TelaPrincipal(
                    nomeUsuario = usuarioLogado?.nome ?: "",
                    tipoUsuario = usuarioLogado?.tipo ?: TipoUsuario.ALUNO,
                    eventosInscritos = eventosInscritos,
                    aoSair = { usuarioLogado = null },
                    aoTrocarAba = { abaSelecionada = it },
                    aoCancelarInscricao = { evento ->
                        eventosInscritos = eventosInscritos - evento
                        eventosDisponiveis = eventosDisponiveis + evento
                    }
                )
            }
            "eventos" -> {
                TelaNovosEventos(
                    eventosDisponiveis = eventosDisponiveis,
                    aoInscrever = { evento ->
                        eventosInscritos = eventosInscritos + evento
                        eventosDisponiveis = eventosDisponiveis.filter { it != evento }
                    },
                    aoTrocarAba = { abaSelecionada = it }
                )
            }
            else -> {
                TelaPrincipal(
                    nomeUsuario = usuarioLogado?.nome ?: "",
                    tipoUsuario = usuarioLogado?.tipo ?: TipoUsuario.ALUNO,
                    eventosInscritos = eventosInscritos,
                    aoSair = { usuarioLogado = null },
                    aoTrocarAba = { abaSelecionada = it },
                    aoCancelarInscricao = { evento ->
                        eventosInscritos = eventosInscritos - evento
                        eventosDisponiveis = eventosDisponiveis + evento
                    }
                )
            }
        }
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

package com.afya.aplicativo_de_verificao_de_presena

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AfyaMagenta
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AplicativodeVerificação_de_PresençaTheme

@Composable
fun TelaNovosEventos(
    eventosDisponiveis: List<Evento>,
    aoInscrever: (Evento) -> Unit,
    aoTrocarAba: (String) -> Unit
) {
    var eventoSelecionadoParaDetalhes by remember { mutableStateOf<Evento?>(null) }
    var mensagemSucesso by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F8F8))
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    "Início", 
                    color = Color(0xFF5A5A5A), 
                    modifier = Modifier.clickable { aoTrocarAba("inicio") }
                )
                Text(
                    "Eventos", 
                    color = AfyaMagenta, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { aoTrocarAba("eventos") }
                )
                Text(
                    "Perfil", 
                    color = Color(0xFF5A5A5A),
                    modifier = Modifier.clickable { aoTrocarAba("perfil") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(AfyaMagenta)
                    .padding(24.dp)
            ) {
                Text(
                    "Novos Eventos",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Descubra o que está acontecendo na Afya.",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                if (mensagemSucesso.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(
                            mensagemSucesso,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text("Disponíveis para inscrição", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(15.dp))

                if (eventosDisponiveis.isEmpty()) {
                    Text(
                        "Não há novos eventos disponíveis no momento.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                } else {
                    eventosDisponiveis.forEach { evento ->
                        CartaoEvento(
                            tipo = "NOVO",
                            titulo = evento.titulo,
                            detalhe = "${evento.local} · ${evento.data}",
                            acao = "Mais detalhes"
                        ) {
                            eventoSelecionadoParaDetalhes = evento
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }

    // Modal de Detalhes
    if (eventoSelecionadoParaDetalhes != null) {
        val evento = eventoSelecionadoParaDetalhes!!
        AlertDialog(
            onDismissRequest = { eventoSelecionadoParaDetalhes = null },
            confirmButton = {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            aoInscrever(evento)
                            mensagemSucesso = "Inscrição realizada com sucesso em: ${evento.titulo}"
                            eventoSelecionadoParaDetalhes = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta)
                    ) {
                        Text("Inscrever-se")
                    }
                    
                    TextButton(
                        onClick = { eventoSelecionadoParaDetalhes = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Fechar", color = Color.Gray)
                    }
                }
            },
            dismissButton = null,
            title = { Text(evento.titulo, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Local: ${evento.local}", fontWeight = FontWeight.Medium)
                    Text("Data: ${evento.data}", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(12.dp))
                    Text(evento.descricao)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

data class Evento(
    val titulo: String,
    val local: String,
    val data: String,
    val descricao: String
)

@Preview(showBackground = true)
@Composable
fun PreviewTelaNovosEventos() {
    AplicativodeVerificação_de_PresençaTheme {
        TelaNovosEventos(
            eventosDisponiveis = listOf(
                Evento("Evento de Teste", "Local", "Data", "Descrição")
            ),
            aoInscrever = {},
            aoTrocarAba = {}
        )
    }
}

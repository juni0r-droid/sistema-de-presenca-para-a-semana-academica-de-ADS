package com.afya.aplicativo_de_verificao_de_presena

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AfyaMagenta
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AplicativodeVerificação_de_PresençaTheme
import kotlinx.coroutines.launch

@Composable
fun RotaNovosEventos(
    usuario: DadosUsuario,
    aoInscrever: (Evento) -> Unit,
    aoTrocarAba: (String) -> Unit,
    aoEditarEvento: (Evento) -> Unit = {},
    aoExcluirEvento: (Evento) -> Unit = {}
) {
    var eventosDisponiveis by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var erroMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                eventosDisponiveis = RetrofitClient.instance.listarEventos()
                erroMsg = ""
            } catch (e: Exception) {
                android.util.Log.e("API_ERRO", "Erro ao carregar eventos", e)
                erroMsg = "Erro ao carregar eventos da API."
            } finally {
                carregando = false
            }
        }
    }

    if (carregando) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AfyaMagenta)
        }
    } else {
        TelaNovosEventos(
            tipoUsuario = usuario.tipo,
            eventosDisponiveis = eventosDisponiveis,
            erroMsg = erroMsg,
            aoInscrever = { evento ->
                // Chamada real à API para salvar a inscrição do aluno
                scope.launch {
                    try {
                        RetrofitClient.instance.inscreverEvento(
                            eventoId = evento.id,
                            emailAluno = usuario.email
                        )
                        aoInscrever(evento)
                    } catch (e: Exception) {
                        android.util.Log.e("API_ERRO", "Erro ao inscrever", e)
                    }
                }
            },
            aoTrocarAba = aoTrocarAba,
            aoEditarEvento = aoEditarEvento,
            aoExcluirEvento = aoExcluirEvento
        )
    }
}

@Composable
fun TelaNovosEventos(
    tipoUsuario: TipoUsuario = TipoUsuario.ALUNO,
    eventosDisponiveis: List<Evento>,
    erroMsg: String = "",
    aoInscrever: (Evento) -> Unit,
    aoTrocarAba: (String) -> Unit,
    aoEditarEvento: (Evento) -> Unit = {},
    aoExcluirEvento: (Evento) -> Unit = {}
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.afya_logo_white),
                        contentDescription = "Logo Afya",
                        modifier = Modifier
                            .size(70.dp, 35.dp)
                            .padding(end = 16.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (tipoUsuario == TipoUsuario.COORDENADOR) "Gestão de Eventos" else "Novos Eventos",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (tipoUsuario == TipoUsuario.COORDENADOR) "Gerencie todos os eventos." else "Descubra o que está acontecendo.",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                if (erroMsg.isNotEmpty()) {
                    Text(
                        erroMsg,
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

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

                Text(
                    if (tipoUsuario == TipoUsuario.COORDENADOR) "Todos os eventos" else "Disponíveis para inscrição",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
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
                    if (tipoUsuario == TipoUsuario.COORDENADOR) {
                        Button(
                            onClick = {
                                aoEditarEvento(evento)
                                eventoSelecionadoParaDetalhes = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta)
                        ) {
                            Text("Editar Evento")
                        }

                        OutlinedButton(
                            onClick = {
                                aoExcluirEvento(evento)
                                eventoSelecionadoParaDetalhes = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                        ) {
                            Text("Excluir Evento")
                        }
                    } else {
                        Button(
                            onClick = {
                                aoInscrever(evento)
                                mensagemSucesso = "Inscrição realizada com sucesso em: ${evento.titulo}"
                                eventoSelecionadoParaDetalhes = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta, contentColor = Color.White)
                        ) {
                            Text("Inscrever-se")
                        }
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

@Preview(showBackground = true)
@Composable
fun PreviewTelaNovosEventos() {
    AplicativodeVerificação_de_PresençaTheme {
        TelaNovosEventos(
            eventosDisponiveis = listOf(
                Evento(titulo = "Evento de Teste", local = "Local", data = "Data", descricao = "Descrição")
            ),
            aoInscrever = {},
            aoTrocarAba = {}
        )
    }
}
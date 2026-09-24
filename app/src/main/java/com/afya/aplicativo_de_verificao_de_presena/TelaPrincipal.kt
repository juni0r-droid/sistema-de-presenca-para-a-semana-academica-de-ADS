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
fun RotaPrincipal(
    usuario: DadosUsuario,
    aoSair: () -> Unit,
    aoTrocarAba: (String) -> Unit,
    aoCancelarInscricao: (Evento) -> Unit,
    aoIniciarValidacao: (Evento) -> Unit,
    aoCriarEvento: () -> Unit = {},
    aoEditarEvento: (Evento) -> Unit = {},
    aoExcluirEvento: (Evento) -> Unit = {}
) {
    var meusEventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var erroMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Função para buscar a lista atualizada do backend
    val carregarListaEventos = {
        scope.launch {
            try {
                meusEventos = if (usuario.tipo == TipoUsuario.COORDENADOR) {
                    RetrofitClient.instance.listarEventos()
                } else {
                    RetrofitClient.instance.listarMeusEventos(usuario.email)
                }
                erroMsg = ""
            } catch (e: Exception) {
                android.util.Log.e("API_ERRO", "Erro ao carregar eventos", e)
                erroMsg = "Erro ao carregar eventos do servidor."
            } finally {
                carregando = false
            }
        }
    }

    LaunchedEffect(usuario) {
        carregarListaEventos()
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
        TelaPrincipal(
            nomeUsuario = usuario.nome,
            tipoUsuario = usuario.tipo,
            eventosInscritos = meusEventos,
            erroMsg = erroMsg,
            aoSair = aoSair,
            aoTrocarAba = aoTrocarAba,
            aoCancelarInscricao = aoCancelarInscricao,
            aoIniciarValidacao = aoIniciarValidacao,
            aoCriarEvento = aoCriarEvento,
            aoEditarEvento = aoEditarEvento,
            aoExcluirEvento = { eventoParaExcluir ->
                // Chamada de exclusão direta na API FastAPI via Retrofit
                scope.launch {
                    try {
                        RetrofitClient.instance.excluirEvento(eventoParaExcluir.id)
                        meusEventos = meusEventos.filter { it.id != eventoParaExcluir.id }
                    } catch (e: Exception) {
                        android.util.Log.e("API_ERRO", "Erro ao excluir evento", e)
                        erroMsg = "Não foi possível excluir o evento no servidor."
                    }
                }
                aoExcluirEvento(eventoParaExcluir)
            }
        )
    }
}

@Composable
fun TelaPrincipal(
    nomeUsuario: String,
    tipoUsuario: TipoUsuario,
    eventosInscritos: List<Evento>,
    erroMsg: String = "",
    aoSair: () -> Unit,
    aoTrocarAba: (String) -> Unit,
    aoCancelarInscricao: (Evento) -> Unit,
    aoIniciarValidacao: (Evento) -> Unit,
    aoCriarEvento: () -> Unit = {},
    aoEditarEvento: (Evento) -> Unit = {},
    aoExcluirEvento: (Evento) -> Unit = {}
) {
    val primeiroNome = nomeUsuario.split(" ").firstOrNull() ?: ""
    val saudacao = if (tipoUsuario == TipoUsuario.COORDENADOR) {
        "Olá, Coordenador(a) $primeiroNome"
    } else {
        "Olá, $primeiroNome"
    }

    var aviso by remember { mutableStateOf("Selecione um evento para iniciar a validação.") }
    var eventoSelecionadoParaDetalhes by remember { mutableStateOf<Evento?>(null) }

    // Estado para controle do Comprovante de Presença
    var eventoParaComprovante by remember { mutableStateOf<Evento?>(null) }

    Scaffold(containerColor = Color.White) { paddingValues ->
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
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.afya_logo_white),
                        contentDescription = "Afya",
                        modifier = Modifier
                            .width(130.dp)
                            .height(52.dp)
                    )
                }
                Spacer(Modifier.height(29.dp))
                Text(
                    saudacao,
                    color = Color.White,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text("Gerencie a presença dos seus eventos.", color = Color.White, fontSize = 15.sp)
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

                Text(
                    if (tipoUsuario == TipoUsuario.COORDENADOR) "Eventos Recentes" else "Seus Eventos",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (tipoUsuario == TipoUsuario.COORDENADOR) {
                    Button(
                        onClick = aoCriarEvento,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Criar Novo Evento", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(15.dp))

                if (eventosInscritos.isEmpty()) {
                    Text(
                        "Você ainda não possui eventos vinculados.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                } else {
                    eventosInscritos.forEach { evento ->
                        CartaoEvento(
                            tipo = if (evento.validado) "VALIDADO" else if (tipoUsuario == TipoUsuario.COORDENADOR) "GERENCIANDO" else "INSCRITO",
                            titulo = evento.titulo,
                            detalhe = "${evento.local} · ${evento.data}",
                            acao = if (evento.validado) "Ver comprovante" else if (tipoUsuario == TipoUsuario.COORDENADOR) "Gerenciar" else "Mais detalhes"
                        ) {
                            if (evento.validado) {
                                eventoParaComprovante = evento
                            } else {
                                eventoSelecionadoParaDetalhes = evento
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Text(
                    aviso,
                    color = Color(0xFF666666),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 23.dp)
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F8F8))
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    "Início",
                    color = AfyaMagenta,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { aoTrocarAba("inicio") }
                )
                Text(
                    "Eventos",
                    color = Color(0xFF5A5A5A),
                    modifier = Modifier.clickable { aoTrocarAba("eventos") }
                )
                Text(
                    "Perfil",
                    color = Color(0xFF5A5A5A),
                    modifier = Modifier.clickable { aoTrocarAba("perfil") }
                )
            }
        }
    }

    // Modal de Detalhes com Opções de Cancelar, Editar, Excluir e Ver Comprovante
    if (eventoSelecionadoParaDetalhes != null) {
        val evento = eventoSelecionadoParaDetalhes!!
        // Liberado para testes nos eventos do aplicativo
        val eHoraDoEvento = true

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
                        if (!evento.validado) {
                            Button(
                                onClick = {
                                    aoIniciarValidacao(evento)
                                    eventoSelecionadoParaDetalhes = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = eHoraDoEvento,
                                colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta, contentColor = Color.White)
                            ) {
                                Text("Validar presença")
                            }
                        } else {
                            Button(
                                onClick = {
                                    eventoParaComprovante = evento
                                    eventoSelecionadoParaDetalhes = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Ver comprovante")
                            }
                        }

                        if (!evento.validado) {
                            OutlinedButton(
                                onClick = {
                                    aoCancelarInscricao(evento)
                                    eventoSelecionadoParaDetalhes = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                            ) {
                                Text("Cancelar inscrição")
                            }
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
                    if (!eHoraDoEvento && tipoUsuario != TipoUsuario.COORDENADOR) {
                        Text(
                            "A validação ficará disponível apenas no dia do evento.",
                            color = AfyaMagenta,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(evento.descricao)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Modal/Dialog do Comprovante de Presença
    if (eventoParaComprovante != null) {
        val ev = eventoParaComprovante!!
        AlertDialog(
            onDismissRequest = { eventoParaComprovante = null },
            confirmButton = {
                Button(
                    onClick = { eventoParaComprovante = null },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Concluído")
                }
            },
            title = {
                Text(
                    "Comprovante de Presença",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF2E7D32)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Participante: $nomeUsuario", fontWeight = FontWeight.SemiBold)
                    Text("Evento: ${ev.titulo}")
                    Text("Local: ${ev.local}")
                    Text("Data: ${ev.data}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Status: PRESENÇA CONFIRMADA",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        "Código do Comprovante: AFYA-${ev.chaveAcesso}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun CartaoEvento(tipo: String, titulo: String, detalhe: String, acao: String, aoAgir: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(tipo, color = AfyaMagenta, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(7.dp))
            Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(5.dp))
            Text(detalhe, color = Color(0xFF555555), fontSize = 14.sp)
            Button(
                onClick = aoAgir,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AfyaMagenta,
                    contentColor = Color(0xFFE6E6E6)
                ),
                shape = RoundedCornerShape(17.dp)
            ) {
                Text(acao)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTelaPrincipal() {
    AplicativodeVerificação_de_PresençaTheme {
        TelaPrincipal(
            nomeUsuario = "Coordenador Afya",
            tipoUsuario = TipoUsuario.COORDENADOR,
            eventosInscritos = listOf(
                Evento(titulo = "Jornada Acadêmica 2026", local = "Auditório Central", data = "Hoje", descricao = "Descrição")
            ),
            aoSair = {},
            aoTrocarAba = {},
            aoCancelarInscricao = {},
            aoIniciarValidacao = {}
        )
    }
}
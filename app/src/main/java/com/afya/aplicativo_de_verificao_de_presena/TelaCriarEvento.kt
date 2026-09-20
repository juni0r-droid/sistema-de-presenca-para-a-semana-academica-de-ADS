package com.afya.aplicativo_de_verificao_de_presena

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AfyaMagenta
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AplicativodeVerificação_de_PresençaTheme
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun RotaCriarEvento(
    eventoParaEditar: Evento? = null,
    aoCriar: (Evento) -> Unit,
    aoVoltar: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var mensagemErro by rememberSaveable { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    TelaCriarEvento(
        eventoParaEditar = eventoParaEditar,
        mensagemErroExterna = mensagemErro,
        carregando = carregando,
        aoConfirmarSalvar = { novoEvento ->
            scope.launch {
                carregando = true
                try {
                    // Envia o novo evento para a API Python em FastAPI via Retrofit
                    RetrofitClient.instance.criarEvento(novoEvento)
                    mensagemErro = ""
                    aoCriar(novoEvento)
                } catch (e: Exception) {
                    android.util.Log.e("API_ERRO", "Erro ao criar evento", e)
                    mensagemErro = "Erro ao guardar no servidor: ${e.localizedMessage ?: e.message}"
                } finally {
                    carregando = false
                }
            }
        },
        aoVoltar = aoVoltar
    )
}

@Composable
fun TelaCriarEvento(
    eventoParaEditar: Evento? = null,
    mensagemErroExterna: String = "",
    carregando: Boolean = false,
    aoConfirmarSalvar: (Evento) -> Unit = {},
    aoVoltar: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(eventoParaEditar?.titulo ?: "") }
    var descricao by rememberSaveable { mutableStateOf(eventoParaEditar?.descricao ?: "") }
    var data by rememberSaveable { mutableStateOf(eventoParaEditar?.data ?: "") }
    var hora by rememberSaveable { mutableStateOf(eventoParaEditar?.hora ?: "") }
    var local by rememberSaveable { mutableStateOf(eventoParaEditar?.local ?: "") }
    var limiteVagas by rememberSaveable { mutableStateOf(eventoParaEditar?.limiteVagas?.toString() ?: "") }
    var duracao by rememberSaveable { mutableStateOf(eventoParaEditar?.duracao ?: "") }
    var mensagemErroInterna by rememberSaveable { mutableStateOf("") }

    var eventoCriadoSucesso by remember { mutableStateOf<Evento?>(null) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(AfyaMagenta)
                    .padding(24.dp)
            ) {
                Text(
                    if (eventoParaEditar != null) "Editar Evento" else "Criar Novo Evento",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Informações Básicas", fontWeight = FontWeight.Bold, color = AfyaMagenta)

            AfyaTextField(titulo, { titulo = it }, "Título do evento", KeyboardType.Text)
            AfyaTextField(descricao, { descricao = it }, "Descrição curta", KeyboardType.Text)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.weight(1f)) {
                    AfyaTextField(data, { data = it }, "Data (DD/MM)", KeyboardType.Text)
                }
                Box(Modifier.weight(1f)) {
                    AfyaTextField(hora, { hora = it }, "Hora (HH:MM)", KeyboardType.Text)
                }
            }

            AfyaTextField(local, { local = it }, "Local / Sala", KeyboardType.Text)

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.weight(1f)) {
                    AfyaTextField(limiteVagas, { limiteVagas = it }, "Vagas", KeyboardType.Number)
                }
                Box(Modifier.weight(1f)) {
                    AfyaTextField(duracao, { duracao = it }, "Duração (h)", KeyboardType.Text)
                }
            }

            val msgParaExibir = mensagemErroExterna.ifEmpty { mensagemErroInterna }
            if (msgParaExibir.isNotEmpty()) {
                Text(msgParaExibir, color = Color.Red, fontSize = 13.sp)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (titulo.isBlank() || data.isBlank() || hora.isBlank() || local.isBlank()) {
                        mensagemErroInterna = "Por favor, preencha os campos obrigatórios."
                    } else {
                        mensagemErroInterna = ""
                        val novaChave = eventoParaEditar?.chaveAcesso ?: UUID.randomUUID().toString().substring(0, 8).uppercase()
                        val novoEvento = Evento(
                            id = eventoParaEditar?.id ?: UUID.randomUUID().toString(),
                            titulo = titulo.trim(),
                            descricao = descricao.trim(),
                            data = data.trim(),
                            hora = hora.trim(),
                            local = local.trim(),
                            limiteVagas = limiteVagas.toIntOrNull() ?: 0,
                            duracao = duracao.trim(),
                            chaveAcesso = novaChave,
                            validado = eventoParaEditar?.validado ?: false
                        )
                        eventoCriadoSucesso = novoEvento
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                enabled = !carregando,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta)
            ) {
                if (carregando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        if (eventoParaEditar != null) "Salvar Alterações" else "Gerar Evento e QR Code",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(onClick = aoVoltar, modifier = Modifier.fillMaxWidth(), enabled = !carregando) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    }

    if (eventoCriadoSucesso != null) {
        val evento = eventoCriadoSucesso!!
        AlertDialog(
            onDismissRequest = { /* Força fechar pelo botão */ },
            confirmButton = {
                Button(
                    onClick = {
                        aoConfirmarSalvar(evento)
                        eventoCriadoSucesso = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta)
                ) {
                    Text("OK, Salvar")
                }
            },
            title = { Text("Evento Criado!") },
            text = {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text("Chave de acesso gerada:", fontWeight = FontWeight.Bold)
                    Text(
                        evento.chaveAcesso,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AfyaMagenta,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Text(
                        "Esta chave será convertida no QR Code para os alunos validarem a presença às ${evento.hora}.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTelaCriarEvento() {
    AplicativodeVerificação_de_PresençaTheme {
        TelaCriarEvento(aoVoltar = {})
    }
}
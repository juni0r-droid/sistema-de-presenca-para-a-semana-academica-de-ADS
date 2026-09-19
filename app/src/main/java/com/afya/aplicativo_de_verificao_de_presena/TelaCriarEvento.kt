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
import java.util.UUID

@Composable
fun TelaCriarEvento(
    eventoParaEditar: Evento? = null,
    aoCriar: (Evento) -> Unit,
    aoVoltar: () -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(eventoParaEditar?.titulo ?: "") }
    var descricao by rememberSaveable { mutableStateOf(eventoParaEditar?.descricao ?: "") }
    var data by rememberSaveable { mutableStateOf(eventoParaEditar?.data ?: "") }
    var hora by rememberSaveable { mutableStateOf(eventoParaEditar?.hora ?: "") }
    var local by rememberSaveable { mutableStateOf(eventoParaEditar?.local ?: "") }
    var limiteVagas by rememberSaveable { mutableStateOf(eventoParaEditar?.limiteVagas?.toString() ?: "") }
    var duracao by rememberSaveable { mutableStateOf(eventoParaEditar?.duracao ?: "") }
    var mensagemErro by rememberSaveable { mutableStateOf("") }

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

            if (mensagemErro.isNotEmpty()) {
                Text(mensagemErro, color = Color.Red, fontSize = 13.sp)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (titulo.isBlank() || data.isBlank() || hora.isBlank() || local.isBlank()) {
                        mensagemErro = "Por favor, preencha os campos obrigatórios."
                    } else {
                        val novaChave = eventoParaEditar?.chaveAcesso ?: UUID.randomUUID().toString().substring(0, 8).uppercase()
                        val novoEvento = Evento(
                            id = eventoParaEditar?.id ?: UUID.randomUUID().toString(),
                            titulo = titulo,
                            descricao = descricao,
                            data = data,
                            hora = hora,
                            local = local,
                            limiteVagas = limiteVagas.toIntOrNull() ?: 0,
                            duracao = duracao,
                            chaveAcesso = novaChave,
                            validado = eventoParaEditar?.validado ?: false
                        )
                        eventoCriadoSucesso = novoEvento
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta)
            ) {
                Text(
                    if (eventoParaEditar != null) "Salvar Alterações" else "Gerar Evento e QR Code", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(onClick = aoVoltar, modifier = Modifier.fillMaxWidth()) {
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
                        aoCriar(evento)
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
        TelaCriarEvento(aoCriar = {}, aoVoltar = {})
    }
}

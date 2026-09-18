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

@Composable
fun TelaPrincipal(
    nomeUsuario: String,
    tipoUsuario: TipoUsuario,
    aoSair: () -> Unit
) {
    val primeiroNome = nomeUsuario.split(" ").firstOrNull() ?: ""
    val saudacao = if (tipoUsuario == TipoUsuario.COORDENADOR) {
        "Olá, Coordenador(a) $primeiroNome"
    } else {
        "Olá, $primeiroNome"
    }
    
    var aviso by remember { mutableStateOf("Selecione um evento para iniciar a validação.") }
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
                    Text(
                        "Sair",
                        color = Color.White,
                        modifier = Modifier
                            .clickable { aoSair() }
                            .padding(8.dp)
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
                Text("Eventos de hoje", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(15.dp))
                CartaoEvento(
                    tipo = "EM ANDAMENTO",
                    titulo = "Jornada Acadêmica 2026",
                    detalhe = "Auditório Central · 09:00 às 17:00",
                    acao = "Validar presença"
                ) { aviso = "Validação de presença iniciada para Jornada Acadêmica 2026." }
                Spacer(Modifier.height(13.dp))
                CartaoEvento(
                    tipo = "PRÓXIMO EVENTO",
                    titulo = "Recepção de novos alunos",
                    detalhe = "Campus Sul · 14:00 às 16:00",
                    acao = "Ver evento"
                ) { aviso = "Detalhes do evento selecionado." }
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
                Text("Início", color = AfyaMagenta, fontWeight = FontWeight.Bold)
                Text("Eventos", color = Color(0xFF5A5A5A))
                Text("Perfil", color = Color(0xFF5A5A5A))
            }
        }
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
            aoSair = {}
        )
    }
}

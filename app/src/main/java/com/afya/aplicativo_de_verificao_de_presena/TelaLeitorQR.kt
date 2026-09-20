package com.afya.aplicativo_de_verificao_de_presena

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AfyaMagenta
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AplicativodeVerificação_de_PresençaTheme
import kotlinx.coroutines.launch

@Composable
fun RotaLeitorQR(
    usuario: DadosUsuario,
    eventoId: String,
    chaveAcessoEvento: String,
    aoValidarSucesso: () -> Unit,
    aoValidarErro: () -> Unit,
    aoCancelar: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var carregando by remember { mutableStateOf(false) }
    var mensagemFeedback by remember { mutableStateOf("") }

    val validarNoServidor = { chaveParaValidar: String ->
        scope.launch {
            carregando = true
            try {
                val req = ValidarQRRequest(
                    evento_id = eventoId,
                    chaveAcesso = chaveParaValidar
                )
                val resposta = RetrofitClient.instance.validarQR(
                    body = req,
                    emailAluno = usuario.email
                )

                if (resposta.sucesso == true) {
                    aoValidarSucesso()
                } else {
                    mensagemFeedback = resposta.mensagem ?: "Chave de acesso inválida."
                    aoValidarErro()
                }
            } catch (e: Exception) {
                android.util.Log.e("API_ERRO", "Erro ao validar QR Code", e)
                mensagemFeedback = "Erro ao validar no servidor."
                aoValidarErro()
            } finally {
                carregando = false
            }
        }
    }

    TelaLeitorQR(
        mensagemFeedback = mensagemFeedback,
        carregando = carregando,
        aoValidarSucesso = { validarNoServidor(chaveAcessoEvento) },
        aoValidarErro = { validarNoServidor("CHAVE_INVALIDA_TESTE") },
        aoCancelar = aoCancelar
    )
}

@Composable
fun TelaLeitorQR(
    mensagemFeedback: String = "",
    carregando: Boolean = false,
    aoValidarSucesso: () -> Unit,
    aoValidarErro: () -> Unit,
    aoCancelar: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Simulação da Câmera (Fundo escuro)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Aponte sua câmera para o QRcode para validar sua presença!",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Mira/Moldura do QR Code
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .border(BorderStroke(4.dp, AfyaMagenta), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (carregando) {
                    CircularProgressIndicator(color = AfyaMagenta)
                }
            }

            if (mensagemFeedback.isNotEmpty()) {
                Text(
                    text = mensagemFeedback,
                    color = Color.Yellow,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botões de Simulação (Para teste do fluxo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = aoValidarSucesso,
                    enabled = !carregando,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Simular Sucesso")
                }

                Button(
                    onClick = aoValidarErro,
                    enabled = !carregando,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Simular Erro")
                }
            }

            TextButton(
                onClick = aoCancelar,
                enabled = !carregando,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text("Cancelar", color = Color.White)
            }
        }
    }
}

@Preview
@Composable
fun PreviewTelaLeitorQR() {
    AplicativodeVerificação_de_PresençaTheme {
        TelaLeitorQR(aoValidarSucesso = {}, aoValidarErro = {}, aoCancelar = {})
    }
}
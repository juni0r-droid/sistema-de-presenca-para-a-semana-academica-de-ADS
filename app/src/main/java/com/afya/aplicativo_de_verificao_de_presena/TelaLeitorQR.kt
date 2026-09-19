package com.afya.aplicativo_de_verificao_de_presena

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun TelaLeitorQR(
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
                // Cantos da mira (opcional, aqui é uma borda simples)
            }
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Botões de Simulação (Para teste do fluxo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = aoValidarSucesso,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Simular Sucesso")
                }
                
                Button(
                    onClick = aoValidarErro,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Simular Erro")
                }
            }
            
            TextButton(
                onClick = aoCancelar,
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

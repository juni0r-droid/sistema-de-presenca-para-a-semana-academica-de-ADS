package com.afya.aplicativo_de_verificao_de_presena

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AfyaMagenta
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AplicativodeVerificação_de_PresençaTheme

@Composable
fun CabecalhoMarca() {
    Row(
        Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(AfyaMagenta),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.afya_logo_white),
            contentDescription = "Afya",
            modifier = Modifier
                .width(220.dp)
                .height(90.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AfyaTextField(
    valor: String,
    aoMudarValor: (String) -> Unit,
    textoSugestao: String,
    tipoTeclado: KeyboardType
) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoMudarValor,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(textoSugestao, color = Color(0xFF555555)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        shape = RoundedCornerShape(24.dp),
        colors = afyaCoresCampo()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun afyaCoresCampo() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color(0xFFD7D7D7),
    unfocusedContainerColor = Color(0xFFD7D7D7),
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    cursorColor = AfyaMagenta
)

@Preview(showBackground = true)
@Composable
fun PreviewComponentes() {
    AplicativodeVerificação_de_PresençaTheme {
        Column(Modifier.padding(16.dp)) {
            CabecalhoMarca()
            Spacer(Modifier.height(16.dp))
            AfyaTextField(
                valor = "",
                aoMudarValor = {},
                textoSugestao = "Exemplo de campo",
                tipoTeclado = KeyboardType.Text
            )
        }
    }
}

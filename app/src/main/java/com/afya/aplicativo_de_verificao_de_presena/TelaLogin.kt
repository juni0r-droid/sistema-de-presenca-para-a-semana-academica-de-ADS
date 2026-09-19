package com.afya.aplicativo_de_verificao_de_presena

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AfyaMagenta
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AplicativodeVerificação_de_PresençaTheme

@Composable
fun RotaLogin(aoLogar: (DadosUsuario) -> Unit, aoIrParaCadastro: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var senhaVisivel by rememberSaveable { mutableStateOf(false) }
    var mensagem by rememberSaveable { mutableStateOf("") }

    TelaLogin(
        email = email,
        senha = senha,
        senhaVisivel = senhaVisivel,
        mensagem = mensagem,
        aoMudarEmail = { email = it },
        aoMudarSenha = { senha = it },
        aoMudarVisibilidadeSenha = { senhaVisivel = !senhaVisivel },
        aoRecuperarSenha = { mensagem = "As instruções serão enviadas ao seu e-mail." },
        aoFazerLogin = {
            if (email.isBlank() || senha.isBlank()) {
                mensagem = "Preencha e-mail e senha para entrar."
            } else {
                mensagem = ""
                // Simulação de login
                if (email == "coordenador@afya.edu.br" && senha == "123456") {
                    aoLogar(DadosUsuario(nome = "Coordenador Afya", email = email, tipo = TipoUsuario.COORDENADOR, cpf = "123.456.789-00"))
                } else {
                    val nomeExtraido = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                    aoLogar(DadosUsuario(nome = nomeExtraido, email = email, tipo = TipoUsuario.ALUNO, registro = "20260001"))
                }
            }
        },
        aoIrParaCadastro = aoIrParaCadastro
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaLogin(
    email: String,
    senha: String,
    senhaVisivel: Boolean,
    mensagem: String,
    aoMudarEmail: (String) -> Unit,
    aoMudarSenha: (String) -> Unit,
    aoMudarVisibilidadeSenha: () -> Unit,
    aoRecuperarSenha: () -> Unit,
    aoFazerLogin: () -> Unit,
    aoIrParaCadastro: () -> Unit
) {
    Scaffold(containerColor = Color.White) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            CabecalhoMarca()
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 40.dp)
            ) {
                Text("Bem-vindo(a)", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Entre para validar presenças e acompanhar os eventos da sua instituição.",
                    color = Color(0xFF555555),
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
                Spacer(Modifier.height(18.dp))
                Text("E-mail", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(9.dp))
                AfyaTextField(email, aoMudarEmail, "email", KeyboardType.Email)
                Spacer(Modifier.height(12.dp))
                Text("Senha", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(9.dp))
                OutlinedTextField(
                    value = senha,
                    onValueChange = aoMudarSenha,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("senha", color = Color(0xFF555555)) },
                    visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(24.dp),
                    colors = afyaCoresCampo(),
                    trailingIcon = {
                        Text(
                            if (senhaVisivel) "Ocultar" else "Mostrar",
                            color = AfyaMagenta,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = aoMudarVisibilidadeSenha)
                                .padding(10.dp)
                        )
                    }
                )
                Text(
                    "Esqueci minha senha",
                    color = AfyaMagenta,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp, bottom = 20.dp)
                        .clickable(onClick = aoRecuperarSenha)
                )
                Button(
                    onClick = aoFazerLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AfyaMagenta,
                        contentColor = Color(0xFFE6E6E6)
                    )
                ) {
                    Text("Entrar", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                if (mensagem.isNotEmpty()) {
                    Text(
                        mensagem,
                        color = AfyaMagenta,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 12.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Não tem uma conta? ", color = Color(0xFF555555), fontSize = 14.sp)
                    Text(
                        "Crie uma aqui",
                        color = AfyaMagenta,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = aoIrParaCadastro)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4F0)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(Modifier.padding(15.dp)) {
                        Text(
                            "Acesso de demonstração",
                            color = AfyaMagenta,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            "coordenador@afya.edu.br\nSenha: 123456",
                            color = Color(0xFF5E1235),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTelaLogin() {
    AplicativodeVerificação_de_PresençaTheme {
        RotaLogin(aoLogar = { }, aoIrParaCadastro = {})
    }
}

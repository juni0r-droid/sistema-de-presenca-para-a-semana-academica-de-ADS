package com.afya.aplicativo_de_verificao_de_presena

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun RotaCadastro(aoCadastrar: (DadosUsuario) -> Unit, aoVoltar: () -> Unit) {
    var tipoSelecionado by rememberSaveable { mutableStateOf(TipoUsuario.ALUNO) }
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var registro by rememberSaveable { mutableStateOf("") }
    var cpf by rememberSaveable { mutableStateOf("") }
    var senhaInstitucional by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var senhaVisivel by rememberSaveable { mutableStateOf(false) }
    var mensagem by rememberSaveable { mutableStateOf("") }

    TelaCadastro(
        tipoSelecionado = tipoSelecionado,
        nome = nome,
        email = email,
        registro = registro,
        cpf = cpf,
        senhaInstitucional = senhaInstitucional,
        senha = senha,
        senhaVisivel = senhaVisivel,
        mensagem = mensagem,
        aoMudarTipo = { tipoSelecionado = it },
        aoMudarNome = { nome = it },
        aoMudarEmail = { email = it },
        aoMudarRegistro = { registro = it },
        aoMudarCpf = { cpf = it },
        aoMudarSenhaInstitucional = { senhaInstitucional = it },
        aoMudarSenha = { senha = it },
        aoMudarVisibilidadeSenha = { senhaVisivel = !senhaVisivel },
        aoCadastrar = {
            val camposValidos = if (tipoSelecionado == TipoUsuario.ALUNO) {
                nome.isNotBlank() && email.isNotBlank() && registro.isNotBlank() && senha.isNotBlank()
            } else {
                nome.isNotBlank() && email.isNotBlank() && cpf.isNotBlank() && 
                senhaInstitucional == "ADM_Afya" && senha.isNotBlank()
            }

            if (!camposValidos) {
                mensagem = if (tipoSelecionado == TipoUsuario.COORDENADOR && senhaInstitucional != "ADM_Afya") {
                    "Senha institucional incorreta."
                } else {
                    "Preencha todos os campos para cadastrar."
                }
            } else {
                mensagem = ""
                aoCadastrar(
                    DadosUsuario(
                        nome = nome,
                        email = email,
                        tipo = tipoSelecionado,
                        registro = if (tipoSelecionado == TipoUsuario.ALUNO) registro else null,
                        cpf = if (tipoSelecionado == TipoUsuario.COORDENADOR) cpf else null
                    )
                )
            }
        },
        aoVoltar = aoVoltar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastro(
    tipoSelecionado: TipoUsuario,
    nome: String,
    email: String,
    registro: String,
    cpf: String,
    senhaInstitucional: String,
    senha: String,
    senhaVisivel: Boolean,
    mensagem: String,
    aoMudarTipo: (TipoUsuario) -> Unit,
    aoMudarNome: (String) -> Unit,
    aoMudarEmail: (String) -> Unit,
    aoMudarRegistro: (String) -> Unit,
    aoMudarCpf: (String) -> Unit,
    aoMudarSenhaInstitucional: (String) -> Unit,
    aoMudarSenha: (String) -> Unit,
    aoMudarVisibilidadeSenha: () -> Unit,
    aoCadastrar: () -> Unit,
    aoVoltar: () -> Unit
) {
    Scaffold(containerColor = Color.White) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            CabecalhoMarca()
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 20.dp)
            ) {
                Text("Crie sua conta", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Cadastre-se para começar a validar presenças nos eventos.",
                    color = Color(0xFF555555),
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )
                Spacer(Modifier.height(16.dp))

                // Seleção de Tipo de Usuário
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val corAluno = if (tipoSelecionado == TipoUsuario.ALUNO) AfyaMagenta else Color(0xFFD7D7D7)
                    val corTextoAluno = if (tipoSelecionado == TipoUsuario.ALUNO) Color.White else Color.Black
                    
                    val corCoordenador = if (tipoSelecionado == TipoUsuario.COORDENADOR) AfyaMagenta else Color(0xFFD7D7D7)
                    val corTextoCoordenador = if (tipoSelecionado == TipoUsuario.COORDENADOR) Color.White else Color.Black

                    Button(
                        onClick = { aoMudarTipo(TipoUsuario.ALUNO) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = corAluno, contentColor = corTextoAluno),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Aluno", fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = { aoMudarTipo(TipoUsuario.COORDENADOR) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = corCoordenador, contentColor = corTextoCoordenador),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Coordenador", fontWeight = FontWeight.Medium)
                    }
                }

                Text("Nome completo", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                AfyaTextField(nome, aoMudarNome, "nome completo", KeyboardType.Text)
                
                Spacer(Modifier.height(8.dp))
                Text("E-mail", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                AfyaTextField(email, aoMudarEmail, "e-mail acadêmico", KeyboardType.Email)
                
                Spacer(Modifier.height(8.dp))
                
                if (tipoSelecionado == TipoUsuario.ALUNO) {
                    Text("Registro do aluno (RA)", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    AfyaTextField(registro, aoMudarRegistro, "número do RA", KeyboardType.Number)
                } else {
                    Text("CPF", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    AfyaTextField(cpf, aoMudarCpf, "número do CPF", KeyboardType.Number)
                    
                    Spacer(Modifier.height(8.dp))
                    Text("Senha Institucional", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = senhaInstitucional,
                        onValueChange = aoMudarSenhaInstitucional,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("senha fornecida pela Afya", color = Color(0xFF555555)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(24.dp),
                        colors = afyaCoresCampo()
                    )
                }
                
                Spacer(Modifier.height(8.dp))
                Text("Senha", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
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
                
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = aoCadastrar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AfyaMagenta,
                        contentColor = Color(0xFFE6E6E6)
                    )
                ) {
                    Text("Cadastrar", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                    Text("Já tem uma conta? ", color = Color(0xFF555555), fontSize = 14.sp)
                    Text(
                        "Faça login",
                        color = AfyaMagenta,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(onClick = aoVoltar)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTelaCadastro() {
    AplicativodeVerificação_de_PresençaTheme {
        RotaCadastro(aoCadastrar = { }, aoVoltar = {})
    }
}

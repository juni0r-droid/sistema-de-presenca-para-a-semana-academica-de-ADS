package com.afya.aplicativo_de_verificao_de_presena

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AfyaMagenta
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AplicativodeVerificação_de_PresençaTheme
import kotlinx.coroutines.launch

@Composable
fun TelaPerfil(
    dadosUsuario: DadosUsuario,
    aoSair: () -> Unit,
    aoTrocarAba: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados para controle do Modal de Alteração de Senha
    var exibirDialogoSenha by remember { mutableStateOf(false) }
    var senhaAtual by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

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
                    color = Color(0xFF555555),
                    modifier = Modifier.clickable { aoTrocarAba("inicio") }
                )
                Text(
                    "Eventos",
                    color = Color(0xFF555555),
                    modifier = Modifier.clickable { aoTrocarAba("eventos") }
                )
                Text(
                    "Perfil",
                    color = AfyaMagenta,
                    fontWeight = FontWeight.Bold,
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
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(AfyaMagenta)
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.afya_logo_white),
                    contentDescription = "Logo Afya",
                    modifier = Modifier.size(100.dp, 35.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = dadosUsuario.nome,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (dadosUsuario.tipo == TipoUsuario.COORDENADOR) "Coordenador(a)" else "Aluno(a)",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Minhas Informações",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ItemInformacao(
                    icone = Icons.Default.Person,
                    rotulo = "Nome",
                    valor = dadosUsuario.nome
                )

                if (dadosUsuario.tipo == TipoUsuario.ALUNO) {
                    ItemInformacao(
                        icone = Icons.Default.AccountCircle,
                        rotulo = "RA",
                        valor = dadosUsuario.registro ?: "Não informado"
                    )
                } else {
                    ItemInformacao(
                        icone = Icons.Default.AccountCircle,
                        rotulo = "CPF",
                        valor = dadosUsuario.cpf ?: "Não informado"
                    )
                }

                ItemInformacao(
                    icone = Icons.Default.Email,
                    rotulo = "E-mail",
                    valor = dadosUsuario.email
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    "Configurações",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { exibirDialogoSenha = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F3F3), contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AfyaMagenta)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Alterar Senha")
                    }
                }

                Button(
                    onClick = aoSair,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Sair da Conta")
                    }
                }
            }
        }
    }

    // --- DIÁLOGO / MODAL PARA ALTERAÇÃO DE SENHA ---
    if (exibirDialogoSenha) {
        AlertDialog(
            onDismissRequest = {
                if (!carregando) {
                    exibirDialogoSenha = false
                    senhaAtual = ""
                    novaSenha = ""
                }
            },
            title = {
                Text(text = "Alterar Senha", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Digite sua senha atual e a nova senha desejada.")

                    OutlinedTextField(
                        value = senhaAtual,
                        onValueChange = { senhaAtual = it },
                        label = { Text("Senha Atual") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = novaSenha,
                        onValueChange = { novaSenha = it },
                        label = { Text("Nova Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (senhaAtual.isBlank() || novaSenha.isBlank()) {
                            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        carregando = true
                        scope.launch {
                            try {
                                val req = AlterarSenhaRequest(
                                    email = dadosUsuario.email,
                                    senhaAntiga = senhaAtual,
                                    novaSenha = novaSenha
                                )

                                val resposta = RetrofitClient.instance.alterarSenha(req)

                                if (resposta.sucesso == true || resposta.mensagem?.contains("sucesso", ignoreCase = true) == true) {
                                    Toast.makeText(context, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()
                                    exibirDialogoSenha = false
                                    senhaAtual = ""
                                    novaSenha = ""
                                } else {
                                    Toast.makeText(context, resposta.mensagem ?: "Erro ao alterar senha", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: retrofit2.HttpException) {
                                if (e.code() == 400) {
                                    Toast.makeText(context, "A senha atual está incorreta.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Erro no servidor (${e.code()})", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro de conexão com o servidor.", Toast.LENGTH_SHORT).show()
                            } finally {
                                carregando = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta),
                    enabled = !carregando
                ) {
                    if (carregando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Salvar")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        exibirDialogoSenha = false
                        senhaAtual = ""
                        novaSenha = ""
                    },
                    enabled = !carregando
                ) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun ItemInformacao(icone: ImageVector, rotulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = AfyaMagenta,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = rotulo, color = Color.Gray, fontSize = 12.sp)
            Text(text = valor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTelaPerfil() {
    AplicativodeVerificação_de_PresençaTheme {
        TelaPerfil(
            dadosUsuario = DadosUsuario(
                nome = "João da Silva",
                email = "joao.silva@afya.edu.br",
                tipo = TipoUsuario.ALUNO,
                registro = "202612345"
            ),
            aoSair = {},
            aoTrocarAba = {}
        )
    }
}
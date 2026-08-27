package com.afya.aplicativo_de_verificao_de_presena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AfyaMagenta
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AplicativodeVerificação_de_PresençaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AplicativodeVerificação_de_PresençaTheme { AfyaEventosApp() } }
    }
}

@Composable
fun AfyaEventosApp() {
    var authenticated by rememberSaveable { mutableStateOf(false) }
    if (authenticated) {
        HomeScreen { authenticated = false }
    } else {
        LoginRoute(onLogin = { authenticated = true })
    }
}

@Composable
fun LoginRoute(onLogin: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf("") }

    LoginScreen(
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        message = message,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
        onRecoverPassword = { message = "As instruções serão enviadas ao seu e-mail." },
        onLogin = {
            if (email.isBlank() || password.isBlank()) {
                message = "Preencha e-mail e senha para entrar."
            } else {
                message = ""
                onLogin()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    email: String,
    password: String,
    passwordVisible: Boolean,
    message: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onRecoverPassword: () -> Unit,
    onLogin: () -> Unit
) {
    Scaffold(containerColor = Color.White) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {
            BrandHeader()
            Column(Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 40.dp)) {
                Text("Bem-vindo(a)", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("Entre para validar presenças e acompanhar os eventos da sua instituição.", color = Color(0xFF555555), fontSize = 15.sp, lineHeight = 21.sp)
                Spacer(Modifier.height(18.dp))
                Text("E-mail", fontWeight = FontWeight.Medium); Spacer(Modifier.height(9.dp))
                AfyaTextField(email, onEmailChange, "email", KeyboardType.Email)
                Spacer(Modifier.height(12.dp)); Text("Senha", fontWeight = FontWeight.Medium); Spacer(Modifier.height(9.dp))
                OutlinedTextField(
                    value = password, onValueChange = onPasswordChange, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    placeholder = { Text("senha", color = Color(0xFF555555)) }, visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), shape = RoundedCornerShape(24.dp), colors = afyaFieldColors(),
                    trailingIcon = { Text(if (passwordVisible) "Ocultar" else "Mostrar", color = AfyaMagenta, fontWeight = FontWeight.Medium, modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable(onClick = onPasswordVisibilityChange).padding(10.dp)) }
                )
                Text("Esqueci minha senha", color = AfyaMagenta, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.align(Alignment.End).padding(top = 8.dp, bottom = 20.dp).clickable(onClick = onRecoverPassword))
                Button(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta, contentColor = Color(0xFFE6E6E6))) { Text("Entrar", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                if (message.isNotEmpty()) Text(message, color = AfyaMagenta, fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 12.dp))
                Spacer(Modifier.height(20.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4F0)), shape = RoundedCornerShape(14.dp)) { Column(Modifier.padding(15.dp)) { Text("Acesso de demonstração", color = AfyaMagenta, fontWeight = FontWeight.Bold, fontSize = 13.sp); Text("coordenador@afya.edu.br\nSenha: 123456", color = Color(0xFF5E1235), fontSize = 13.sp, lineHeight = 19.sp) } }
            }
        }
    }
}

@Composable
fun BrandHeader() {
    Row(Modifier.fillMaxWidth().height(130.dp).background(AfyaMagenta), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Image(painter = painterResource(R.drawable.afya_logo_white), contentDescription = "Afya", modifier = Modifier.width(220.dp).height(90.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AfyaTextField(value: String, onValueChange: (String) -> Unit, placeholder: String, keyboardType: KeyboardType) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), placeholder = { Text(placeholder, color = Color(0xFF555555)) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = keyboardType), shape = RoundedCornerShape(24.dp), colors = afyaFieldColors())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun afyaFieldColors() = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color(0xFFD7D7D7), unfocusedContainerColor = Color(0xFFD7D7D7), focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, cursorColor = AfyaMagenta)

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    var notice by remember { mutableStateOf("Selecione um evento para iniciar a validação.") }
    Scaffold(containerColor = Color.White) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {
            Column(Modifier.fillMaxWidth().background(AfyaMagenta).padding(24.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Image(painter = painterResource(R.drawable.afya_logo_white), contentDescription = "Afya", modifier = Modifier.width(130.dp).height(52.dp)); Text("Sair", color = Color.White, modifier = Modifier.clickable { onLogout() }.padding(8.dp)) }
                Spacer(Modifier.height(29.dp)); Text("Olá, Coordenador(a)", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.SemiBold); Text("Gerencie a presença dos seus eventos.", color = Color.White, fontSize = 15.sp)
            }
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(24.dp)) {
                Text("Eventos de hoje", fontSize = 19.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(15.dp))
                EventCard("EM ANDAMENTO", "Jornada Acadêmica 2026", "Auditório Central · 09:00 às 17:00", "Validar presença") { notice = "Validação de presença iniciada para Jornada Acadêmica 2026." }
                Spacer(Modifier.height(13.dp)); EventCard("PRÓXIMO EVENTO", "Recepção de novos alunos", "Campus Sul · 14:00 às 16:00", "Ver evento") { notice = "Detalhes do evento selecionado." }
                Text(notice, color = Color(0xFF666666), fontSize = 14.sp, modifier = Modifier.fillMaxWidth().padding(top = 23.dp))
            }
            Row(Modifier.fillMaxWidth().background(Color(0xFFF8F8F8)).padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceAround) { Text("Início", color = AfyaMagenta, fontWeight = FontWeight.Bold); Text("Eventos", color = Color(0xFF5A5A5A)); Text("Perfil", color = Color(0xFF5A5A5A)) }
        }
    }
}

@Composable
fun EventCard(type: String, title: String, detail: String, action: String, onAction: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp)) { Text(type, color = AfyaMagenta, fontSize = 12.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(7.dp)); Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(5.dp)); Text(detail, color = Color(0xFF555555), fontSize = 14.sp); Button(onClick = onAction, modifier = Modifier.fillMaxWidth().padding(top = 15.dp), colors = ButtonDefaults.buttonColors(containerColor = AfyaMagenta, contentColor = Color(0xFFE6E6E6)), shape = RoundedCornerShape(17.dp)) { Text(action) } } }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() { AplicativodeVerificação_de_PresençaTheme { LoginRoute {} } }

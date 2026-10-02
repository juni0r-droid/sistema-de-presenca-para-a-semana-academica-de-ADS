package com.afya.aplicativo_de_verificao_de_presena

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.afya.aplicativo_de_verificao_de_presena.ui.theme.AfyaMagenta
import com.journeyapps.barcodescanner.CompoundBarcodeView
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

    val validarNoServidor = { chaveEscaneada: String ->
        if (!carregando) {
            scope.launch {
                carregando = true
                mensagemFeedback = ""
                try {
                    val chaveLimpa = chaveEscaneada.trim().uppercase()
                    android.util.Log.d("QR_SCANNER", "Chave lida: $chaveLimpa - EventoID: $eventoId")

                    val req = ValidarQRRequest(
                        evento_id = eventoId,
                        chaveAcesso = chaveLimpa
                    )

                    val resposta = RetrofitClient.instance.validarQR(
                        body = req,
                        emailAluno = usuario.email.trim()
                    )

                    if (resposta.sucesso == true) {
                        aoValidarSucesso()
                    } else {
                        mensagemFeedback = resposta.mensagem ?: "Chave de acesso inválida."
                        aoValidarErro()
                    }
                } catch (e: retrofit2.HttpException) {
                    val codigo = e.code()
                    val erroCorpo = e.response()?.errorBody()?.string() ?: ""
                    android.util.Log.e("API_ERRO", "Erro HTTP $codigo: $erroCorpo")

                    mensagemFeedback = when (codigo) {
                        400 -> "Chave do QR Code incorreta."
                        404 -> "Evento não encontrado no servidor."
                        else -> "Erro na validação ($codigo)."
                    }
                    aoValidarErro()
                } catch (e: Exception) {
                    android.util.Log.e("API_ERRO", "Erro de conexão ao validar QR", e)
                    mensagemFeedback = "Falha na conexão com o servidor."
                    aoValidarErro()
                } finally {
                    carregando = false
                }
            }
        }
    }

    TelaLeitorQR(
        mensagemFeedback = mensagemFeedback,
        carregando = carregando,
        aoCodigoLido = { chaveLida ->
            validarNoServidor(chaveLida)
        },
        aoCancelar = aoCancelar
    )
}

@Composable
fun TelaLeitorQR(
    mensagemFeedback: String = "",
    carregando: Boolean = false,
    aoCodigoLido: (String) -> Unit,
    aoCancelar: () -> Unit
) {
    val context = LocalContext.current
    var temPermissaoCamera by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcherPermissao = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { foiConcedida ->
        temPermissaoCamera = foiConcedida
    }

    LaunchedEffect(Unit) {
        if (!temPermissaoCamera) {
            launcherPermissao.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (temPermissaoCamera) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    CompoundBarcodeView(ctx).apply {
                        setStatusText("") // Limpa o texto padrão do ZXing
                        decodeSingle { result ->
                            val valorLido = result.text
                            if (!valorLido.isNullOrEmpty()) {
                                this.pause() // Para o leitor para não ler múltiplas vezes
                                ContextCompat.getMainExecutor(ctx).execute {
                                    aoCodigoLido(valorLido.trim())
                                }
                            }
                        }
                        resume()
                    }
                }
            )
        }

        // Overlay com texto e botões por cima da câmara
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
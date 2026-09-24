package com.afya.aplicativo_de_verificao_de_presena

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.OkHttpClient

// --- MODELOS DE REQUISIÇÃO E RESPOSTA DA API ---

data class LoginRequest(
    val email: String,
    val senha: String
)

data class CadastroRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val tipo: TipoUsuario,
    val registro: String? = null,
    val cpf: String? = null,
    val senhaInstitucional: String? = null
)

data class AlterarSenhaRequest(
    val email: String,
    val senhaAntiga: String,
    val novaSenha: String
)

data class ValidarQRRequest(
    val evento_id: String,
    val chaveAcesso: String
)

data class RespostaPadrao(
    val mensagem: String? = null,
    val sucesso: Boolean? = null
)

// --- INTERFACE RETROFIT ---

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): DadosUsuario

    @POST("auth/alterar-senha")
    suspend fun alterarSenha(@Body body: AlterarSenhaRequest): RespostaPadrao

    @POST("eventos/{evento_id}/inscrever")
    suspend fun inscreverEvento(
        @Path("evento_id") eventoId: String,
        @Query("email_aluno") emailAluno: String
    ): RespostaPadrao

    @POST("auth/cadastro")
    suspend fun cadastrar(@Body body: CadastroRequest): DadosUsuario

    @GET("eventos")
    suspend fun listarEventos(): List<Evento>

    @GET("eventos/meus-eventos")
    suspend fun listarMeusEventos(@Query("email_aluno") emailAluno: String): List<Evento>

    @POST("eventos")
    suspend fun criarEvento(@Body evento: Evento): Evento

    @DELETE("eventos/{id}")
    suspend fun excluirEvento(@Path("id") id: String): RespostaPadrao

    @POST("eventos/validar-qr")
    suspend fun validarQR(
        @Body body: ValidarQRRequest,
        @Query("email_aluno") emailAluno: String
    ): RespostaPadrao
}

// --- CLIENTE RETROFIT (CONFIGURAÇÃO DE IP) ---
object RetrofitClient {
    // Sua URL do Ngrok:
    private const val BASE_URL = "https://backend-app-validacao.onrender.com/"

    // Adiciona o cabeçalho exigido pelo Ngrok para responder JSON direto
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.addHeader("ngrok-skip-browser-warning", "true")
            chain.proceed(requestBuilder.build())
        }
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
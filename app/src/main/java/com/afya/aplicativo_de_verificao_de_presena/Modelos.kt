package com.afya.aplicativo_de_verificao_de_presena

import java.io.Serializable

enum class TipoUsuario { ALUNO, COORDENADOR }

data class DadosUsuario(
    val nome: String,
    val email: String,
    val tipo: TipoUsuario,
    val registro: String? = null,
    val cpf: String? = null
) : Serializable

data class Evento(
    val id: String = java.util.UUID.randomUUID().toString(),
    val titulo: String,
    val local: String,
    val data: String,
    val hora: String = "",
    val descricao: String,
    val limiteVagas: Int = 0,
    val duracao: String = "",
    val chaveAcesso: String = "",
    var validado: Boolean = false
) : Serializable

package br.com.zupacademy.model.response

import io.micronaut.core.annotation.Introspected

@Introspected
data class ConsultaChavePixContaResponse(
    val instituicao: String?,
    val nomeDoTitular: String?,
    val cpfDoTitular: String?,
    val agencia: String?,
    val numero: String?
)
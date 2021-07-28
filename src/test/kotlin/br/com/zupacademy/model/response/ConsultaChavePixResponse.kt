package br.com.zupacademy.model.response

import br.com.zupacademy.grpc.TipoDeChaveGRPC
import br.com.zupacademy.grpc.TipoDeContaGRPC
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
data class ConsultaChavePixResponse(
    val pixId: String?,
    val tipo: TipoDeChaveGRPC?,
    val chave: String?,
    val criadaEm: LocalDateTime?,
    val tipoConta: TipoDeContaGRPC?,
    val conta: ConsultaChavePixContaResponse?
)


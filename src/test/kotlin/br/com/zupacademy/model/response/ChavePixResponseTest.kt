package br.com.zupacademy.model.response

import br.com.zupacademy.model.TipoDeChave
import br.com.zupacademy.model.TipoDeConta
import com.fasterxml.jackson.annotation.JsonInclude
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime

@Introspected
@JsonInclude(JsonInclude.Include.ALWAYS)
class ChavePixResponseTest(
    val clienteId: String,
    val chaves: List<ChavePixChaveResponseTest>
)

@Introspected
class ChavePixChaveResponseTest(
    val chave: String,
    val id: String,
    val tipo: TipoDeChave,
    val tipoDeConta: TipoDeConta,
    val criadaEm: LocalDateTime
)
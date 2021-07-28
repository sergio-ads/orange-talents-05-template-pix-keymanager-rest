package br.com.zupacademy.model.response

import br.com.zupacademy.grpc.ListaChavePixResponseGRPC
import com.fasterxml.jackson.annotation.JsonInclude
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
@JsonInclude(JsonInclude.Include.ALWAYS)
class ChavePixResponse(chaveGrpc: ListaChavePixResponseGRPC) {
    val clienteId = chaveGrpc.clienteId
    val chaves = chaveGrpc.chavesList?.map { ChavePixChaveResponse(it) }
}

@Introspected
class ChavePixChaveResponse(chave: ListaChavePixResponseGRPC.ChavePix) {
    val id = chave.pixId
    val chave = chave.chave
    val tipo = chave.tipo
    val tipoDeConta = chave.tipoDeConta
    val criadaEm = chave.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}
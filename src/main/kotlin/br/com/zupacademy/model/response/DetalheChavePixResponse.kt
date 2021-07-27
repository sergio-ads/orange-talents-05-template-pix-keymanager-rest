package br.com.zupacademy.model.response

import br.com.zupacademy.grpc.ConsultaChavePixResponseGRPC
import br.com.zupacademy.grpc.TipoDeContaGRPC
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
class DetalheChavePixResponse(chaveGrpc: ConsultaChavePixResponseGRPC) {
    val pixId = chaveGrpc.pixId
    val tipo = chaveGrpc.chave.tipo
    val chave = chaveGrpc.chave.chave

    val criadaEm = chaveGrpc.chave.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }

    val tipoConta = when (chaveGrpc.chave.conta.tipo) {
        TipoDeContaGRPC.CONTA_CORRENTE -> "CONTA_CORRENTE"
        TipoDeContaGRPC.CONTA_POUPANCA -> "CONTA_POUPANCA"
        else -> "NAO_RECONHECIDA"
    }

    val conta = mapOf(Pair("tipo", tipoConta),
        Pair("instituicao", chaveGrpc.chave.conta.instituicao),
        Pair("nomeDoTitular", chaveGrpc.chave.conta.nomeDoTitular),
        Pair("cpfDoTitular", chaveGrpc.chave.conta.cpfDoTitular),
        Pair("agencia", chaveGrpc.chave.conta.agencia),
        Pair("numero", chaveGrpc.chave.conta.numeroDaConta))
}
package br.com.zupacademy.model

import br.com.zupacademy.grpc.TipoDeContaGRPC

enum class TipoDeConta(val tipoGrpc: TipoDeContaGRPC) {
    CONTA_CORRENTE(TipoDeContaGRPC.CONTA_CORRENTE),
    CONTA_POUPANCA(TipoDeContaGRPC.CONTA_POUPANCA)
}
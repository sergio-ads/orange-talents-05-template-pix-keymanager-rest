package br.com.zupacademy.model.request

import br.com.zupacademy.grpc.RegistraChavePixRequestGRPC
import br.com.zupacademy.grpc.TipoDeChaveGRPC
import br.com.zupacademy.grpc.TipoDeContaGRPC
import br.com.zupacademy.model.enum.TipoDeChave
import br.com.zupacademy.model.enum.TipoDeConta
import br.com.zupacademy.validation.ValidPixKey
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class RegistraChavePixRequest (
    @field:NotNull val tipoDeConta: TipoDeConta? = null,
    @field:Size(max = 77) val chave: String? = "",
    @field:NotNull val tipoDeChave: TipoDeChave? = null
) {

    fun toModel(clienteId: String): RegistraChavePixRequestGRPC {
        return RegistraChavePixRequestGRPC.newBuilder()
            .setClienteId(clienteId)
            .setTipoDeConta(tipoDeConta?.tipoGrpc ?: TipoDeContaGRPC.UNKNOWN_TIPO_CONTA)
            .setTipoDeChave(tipoDeChave?.tipoGrpc ?: TipoDeChaveGRPC.UNKNOWN_TIPO_CHAVE)
            .setChave(chave ?: "")
            .build()
    }

}

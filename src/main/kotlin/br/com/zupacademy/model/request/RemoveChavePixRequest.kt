package br.com.zupacademy.model.request

import br.com.zupacademy.grpc.RemoveChavePixRequestGRPC
import br.com.zupacademy.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RemoveChavePixRequest(
    @field:NotBlank @field:ValidUUID val clienteId: String?,
    @field:NotBlank @field:ValidUUID val pixId: String?,
) {

    fun toModel(): RemoveChavePixRequestGRPC {
        return RemoveChavePixRequestGRPC.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .build()
    }

}

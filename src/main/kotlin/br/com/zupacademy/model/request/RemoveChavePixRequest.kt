package br.com.zupacademy.model.request

import br.com.zupacademy.grpc.RemoveChavePixRequestGRPC
import br.com.zupacademy.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
class RemoveChavePixRequest(
    @NotBlank @ValidUUID val clienteId: String?,
    @NotBlank @ValidUUID val pixId: String?,
) {

    fun toModel(): RemoveChavePixRequestGRPC {
        return RemoveChavePixRequestGRPC.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .build()
    }

}

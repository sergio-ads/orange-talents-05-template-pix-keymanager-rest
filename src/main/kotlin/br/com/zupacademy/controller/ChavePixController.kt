package br.com.zupacademy.controller

import br.com.zupacademy.grpc.*
import br.com.zupacademy.model.request.RegistraChavePixRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.Valid

@Validated
@Controller("/api/v1/pix/{clienteId}")
class ChavePixController(
    private val registraClient: KeymanagerRegistraGRPCServiceGrpc.KeymanagerRegistraGRPCServiceBlockingStub,
    private val removeClient: KeymanagerRemoveGRPCServiceGrpc.KeymanagerRemoveGRPCServiceBlockingStub,
    private val listaClient: KeymanagerListaGRPCServiceGrpc.KeymanagerListaGRPCServiceBlockingStub,
    private val consultaClient: KeymanagerConsultaGRPCServiceGrpc.KeymanagerConsultaGRPCServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post
    fun cadastra(
        clienteId: String,
        @Valid @Body request: RegistraChavePixRequest
    ): HttpResponse<Any> {
        LOGGER.info("$clienteId: criando nova chave pix com $request")

        val grpcResponse = registraClient.registraGRPC(request.toModel(clienteId))

        return HttpResponse.created(HttpResponse.uri("/api/v1/pix/$clienteId/${grpcResponse.pixId}"))
    }

}
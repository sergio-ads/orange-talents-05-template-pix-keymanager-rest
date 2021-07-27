package br.com.zupacademy.controller

import br.com.zupacademy.grpc.*
import br.com.zupacademy.model.request.RegistraChavePixRequest
import br.com.zupacademy.model.request.RemoveChavePixRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.Valid

@Validated
@Controller("/api/v1/pix")
class ChavePixController(
    private val registraClient: KeymanagerRegistraGRPCServiceGrpc.KeymanagerRegistraGRPCServiceBlockingStub,
    private val removeClient: KeymanagerRemoveGRPCServiceGrpc.KeymanagerRemoveGRPCServiceBlockingStub,
    private val listaClient: KeymanagerListaGRPCServiceGrpc.KeymanagerListaGRPCServiceBlockingStub,
    private val consultaClient: KeymanagerConsultaGRPCServiceGrpc.KeymanagerConsultaGRPCServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post
    fun cadastra(@Valid @Body request: RegistraChavePixRequest): HttpResponse<Any> {
        val grpcResponse = registraClient.registraGRPC(request.toModel()).also {
            LOGGER.info("Criando nova chave pix com $request")
        }

        return HttpResponse.created(HttpResponse.uri("/api/v1/pix/${grpcResponse.clienteId}/${grpcResponse.pixId}"))
    }

    @Delete
    fun apaga(@Valid @Body request: RemoveChavePixRequest): HttpResponse<Any> {
        val grpcResponse = removeClient.removeGRPC(request.toModel()).also {
            LOGGER.info("Apagando chave pix com $request")
        }

        return HttpResponse.ok()
    }

}
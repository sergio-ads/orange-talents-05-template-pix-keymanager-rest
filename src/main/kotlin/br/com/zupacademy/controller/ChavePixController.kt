package br.com.zupacademy.controller

import br.com.zupacademy.grpc.*
import br.com.zupacademy.model.request.RegistraChavePixRequest
import br.com.zupacademy.model.request.RemoveChavePixRequest
import br.com.zupacademy.model.response.ChavePixResponse
import br.com.zupacademy.model.response.DetalheChavePixResponse
import br.com.zupacademy.validation.ValidUUID
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
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
        removeClient.removeGRPC(request.toModel()).also {
            LOGGER.info("Apagando chave pix com $request")
        }
        return HttpResponse.ok()
    }

    @Get("/{clienteId}/{pixId}")
    fun consulta(
        @PathVariable @ValidUUID clienteId: String?,
        @PathVariable @ValidUUID pixId: String?
    ): HttpResponse<Any> {
        val grpcResponse = consultaClient.consultaGRPC(ConsultaChavePixRequestGRPC.newBuilder()
            .setPixId(
                ConsultaChavePixRequestGRPC.FiltroPixId.newBuilder()
                    .setClienteId(clienteId)
                    .setPixId(pixId)
                    .build()
            )
            .build()
        ).also {
            LOGGER.info("Consultando chave pix com clienteId: $clienteId, pixId: $pixId")
        }
        return HttpResponse.ok(DetalheChavePixResponse(grpcResponse))
    }

    @Get("/{clienteId}")
    fun lista(@PathVariable @ValidUUID clienteId: String?): HttpResponse<Any> {
        val grpcResponse = listaClient.listaGRPC(ListaChavePixRequestGRPC.newBuilder()
            .setClienteId(clienteId)
            .build()
        ).also {
            LOGGER.info("Listando chaves pix do clienteId: $clienteId")
        }
        return HttpResponse.ok(ChavePixResponse(grpcResponse))
    }

}
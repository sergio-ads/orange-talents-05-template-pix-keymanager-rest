package br.com.zupacademy.controller

import br.com.zupacademy.grpc.KeyManagerGrpcFactory
import br.com.zupacademy.grpc.KeymanagerRemoveGRPCServiceGrpc
import br.com.zupacademy.grpc.RemoveChavePixRequestGRPC
import br.com.zupacademy.grpc.RemoveChavePixResponseGRPC
import br.com.zupacademy.model.request.RemoveChavePixRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class RemoveChavePixControllerTest(
    private val grpcClient: KeymanagerRemoveGRPCServiceGrpc.KeymanagerRemoveGRPCServiceBlockingStub
) {
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    val RANDOM_CLIENT_ID = UUID.randomUUID().toString()
    val RANDOM_PIX_ID = UUID.randomUUID().toString()

    @Test
    fun `deve apagar uma chave pix`() {
        // Cenário
        whenever(grpcClient.removeGRPC(RemoveChavePixRequestGRPC.newBuilder()
            .setClienteId(RANDOM_CLIENT_ID)
            .setPixId(RANDOM_PIX_ID)
            .build())
        ).thenReturn(RemoveChavePixResponseGRPC.newBuilder()
            .setClienteId(RANDOM_CLIENT_ID)
            .setPixId(RANDOM_PIX_ID)
            .build()
        )

        // Executa
        val request = HttpRequest.DELETE(
            "/api/v1/pix",
            RemoveChavePixRequest(RANDOM_CLIENT_ID, RANDOM_PIX_ID)
        )
        val response = httpClient.toBlocking().exchange(request, RemoveChavePixRequest::class.java)

        // Valida
        Assertions.assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    fun `deve informar chave pix nao existente`() {
        // Cenário
        val status = Status.fromCode(Status.NOT_FOUND.code)
            .withDescription("Chave pix não encontrada no sistema para este cliente")

        whenever(grpcClient.removeGRPC(RemoveChavePixRequestGRPC.newBuilder()
            .setClienteId(RANDOM_CLIENT_ID)
            .setPixId(RANDOM_PIX_ID)
            .build())
        ).thenThrow(StatusRuntimeException(status))

        // Executa
        val request = HttpRequest.DELETE(
            "/api/v1/pix",
            RemoveChavePixRequest(RANDOM_CLIENT_ID, RANDOM_PIX_ID)
        )

        val thrown = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, RemoveChavePixRequest::class.java)
        }

        // Valida
        assertThat(thrown.status, equalTo(HttpStatus.NOT_FOUND))
        assertThat(thrown.message, equalTo("Chave pix não encontrada no sistema para este cliente"))
    }

    @Test
    fun `deve informar erro de validacao`() {
        // Executa
        val request = HttpRequest.DELETE(
            "/api/v1/pix",
            RemoveChavePixRequest("", "")
        )

        val thrown = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, RemoveChavePixRequest::class.java)
        }

        // Valida
        assertThat(thrown.status, equalTo(HttpStatus.BAD_REQUEST))
    }

    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    class MockitoRemoveFactory {
        @Singleton
        fun removeChaveStubGrpc(): KeymanagerRemoveGRPCServiceGrpc.KeymanagerRemoveGRPCServiceBlockingStub? {
            return Mockito.mock(KeymanagerRemoveGRPCServiceGrpc.KeymanagerRemoveGRPCServiceBlockingStub::class.java)
        }
    }

}
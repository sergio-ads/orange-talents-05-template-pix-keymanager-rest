package br.com.zupacademy.controller

import br.com.zupacademy.grpc.KeyManagerGrpcFactory
import br.com.zupacademy.grpc.KeymanagerRegistraGRPCServiceGrpc
import br.com.zupacademy.grpc.RegistraChavePixResponseGRPC
import br.com.zupacademy.model.TipoDeChave
import br.com.zupacademy.model.TipoDeConta
import br.com.zupacademy.model.request.RegistraChavePixRequest
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraChavePixControllerTest(
    private val grpcClient: KeymanagerRegistraGRPCServiceGrpc.KeymanagerRegistraGRPCServiceBlockingStub
) {
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    val RANDOM_CLIENT_ID = UUID.randomUUID().toString()
    val RANDOM_PIX_ID = UUID.randomUUID().toString()

    @Test
    fun `deve registrar uma nova chave pix`() {
        // Cenário
        `when`(grpcClient.registraGRPC(Mockito.any()))
            .thenReturn(
                RegistraChavePixResponseGRPC.newBuilder()
                    .setClienteId(RANDOM_CLIENT_ID)
                    .setPixId(RANDOM_PIX_ID)
                    .build()
            )

        // Executa
        val request = HttpRequest.POST(
            "/api/v1/pix/$RANDOM_CLIENT_ID",
            RegistraChavePixRequest(
                tipoDeConta = TipoDeConta.CONTA_CORRENTE,
                chave = "tiago.freitas@zup.com.br",
                tipoDeChave = TipoDeChave.EMAIL
            )
        )
        val response = httpClient.toBlocking().exchange(request, RegistraChavePixRequest::class.java)

        // Valida
        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location")!!.contains(RANDOM_PIX_ID))
    }

    @Test
    fun `deve informar erro devido ser null`() {
        // Executa
        val request = HttpRequest.POST(
            "/api/v1/pix/$RANDOM_CLIENT_ID",
            RegistraChavePixRequest(null, null, null)
        )
        val thrown = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, RegistraChavePixRequest::class.java)
        }

        // Valida
        with(thrown) {
            assertEquals(HttpStatus.BAD_REQUEST, status)
        }
    }

    @Test
    fun `deve informar erro devido nao ser uma chave pix valida`() {
        // Executa
        val request = HttpRequest.POST(
            "/api/v1/pix/$RANDOM_CLIENT_ID",
            RegistraChavePixRequest(TipoDeConta.CONTA_CORRENTE, UUID.randomUUID().toString(), TipoDeChave.EMAIL)
        )
        val thrown = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, RegistraChavePixRequest::class.java)
        }

        // Valida
        with(thrown) {
            assertEquals(HttpStatus.BAD_REQUEST, status)
        }
    }

    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    class MockitoRegistraFactory {
        @Singleton
        fun registraChaveStubGrpc(): KeymanagerRegistraGRPCServiceGrpc.KeymanagerRegistraGRPCServiceBlockingStub? {
            return Mockito.mock(KeymanagerRegistraGRPCServiceGrpc.KeymanagerRegistraGRPCServiceBlockingStub::class.java)
        }
    }

}
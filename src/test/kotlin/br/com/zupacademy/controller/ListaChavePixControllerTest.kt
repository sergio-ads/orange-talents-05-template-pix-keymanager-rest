package br.com.zupacademy.controller

import br.com.zupacademy.grpc.*
import br.com.zupacademy.model.response.ChavePixResponseTest
import br.com.zupacademy.model.response.ConsultaChavePixResponse
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.Arrays.asList
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class ListaChavePixControllerTest(
    private val grpcClient: KeymanagerListaGRPCServiceGrpc.KeymanagerListaGRPCServiceBlockingStub
) {
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    val DEFAULT_CLIENT_ID = "bc35591d-b547-4151-a325-4a9d2cd19614"

    @Test
    fun `deve listar 2 chaves pix`() {
        // Cenário
        whenever(grpcClient.listaGRPC(
            ListaChavePixRequestGRPC.newBuilder()
                .setClienteId(DEFAULT_CLIENT_ID)
                .build())
        ).thenReturn(criaResponseGrpc(listOf(chaveEmail, chaveAleatoria)))

        // Executa
        val request = HttpRequest.GET<Any>("/api/v1/pix/$DEFAULT_CLIENT_ID")
        val response = httpClient.toBlocking()
            .exchange(request, ChavePixResponseTest::class.java)

        // Valida
        assertThat(response.status, equalTo(HttpStatus.OK))
        with(response.body.get()) {
            assertThat(this.clienteId, equalTo(DEFAULT_CLIENT_ID))
            assertThat(this.chaves, hasSize(2))
        }
    }

    @Test
    fun `deve listar nenhuma chave pix`() {
        // Cenário
        whenever(grpcClient.listaGRPC(
            ListaChavePixRequestGRPC.newBuilder()
                .setClienteId(DEFAULT_CLIENT_ID)
                .build())
        ).thenReturn(criaResponseGrpc(emptyList()))

        // Executa
        val request = HttpRequest.GET<Any>("/api/v1/pix/$DEFAULT_CLIENT_ID")
        val response = httpClient.toBlocking()
            .exchange(request, ChavePixResponseTest::class.java)

        // Valida
        assertThat(response.status, equalTo(HttpStatus.OK))
        with(response.body.get()) {
            assertThat(this.clienteId, equalTo(DEFAULT_CLIENT_ID))
            assertThat(this.chaves, hasSize(0))
        }
    }

    @Test
    fun `deve informar erro ao passar clienteId nao UUID`() {
        // Executa
        val request = HttpRequest.GET<Any>("/api/v1/pix/aaa")

        val thrown = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }

        // Valida
        assertThat(thrown.status, equalTo(HttpStatus.BAD_REQUEST))
    }

    val chaveEmail = ListaChavePixResponseGRPC.ChavePix.newBuilder()
        .setPixId("2ad82941-f722-4956-981f-924ffde261ea")
        .setTipo(TipoDeChaveGRPC.EMAIL)
        .setChave("tiago.freitas@zup.com.br")
        .setTipoDeConta(TipoDeContaGRPC.CONTA_CORRENTE)
        .setCriadaEm(LocalDateTime.now().let {
            val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
            Timestamp.newBuilder()
                .setSeconds(createdAt.epochSecond)
                .setNanos(createdAt.nano)
                .build()
        })
        .build()

    val chaveAleatoria = ListaChavePixResponseGRPC.ChavePix.newBuilder()
        .setPixId("80bde177-86f5-4f6e-aaea-559f3be19210")
        .setTipo(TipoDeChaveGRPC.ALEATORIA)
        .setChave("cf376a84-0ab1-49ad-9ee9-17336cec276a")
        .setTipoDeConta(TipoDeContaGRPC.CONTA_CORRENTE)
        .setCriadaEm(LocalDateTime.now().let {
            val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
            Timestamp.newBuilder()
                .setSeconds(createdAt.epochSecond)
                .setNanos(createdAt.nano)
                .build()
        })
        .build()

    private fun criaResponseGrpc(chaves: List<ListaChavePixResponseGRPC.ChavePix>): ListaChavePixResponseGRPC {

        return ListaChavePixResponseGRPC.newBuilder()
            .setClienteId("bc35591d-b547-4151-a325-4a9d2cd19614")
            .addAllChaves(chaves)
            .build()
    }

    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    class MockitoListaFactory {
        @Singleton
        fun listaChavesStubGrpc(): KeymanagerListaGRPCServiceGrpc.KeymanagerListaGRPCServiceBlockingStub? {
            return Mockito.mock(KeymanagerListaGRPCServiceGrpc.KeymanagerListaGRPCServiceBlockingStub::class.java)
        }
    }
}
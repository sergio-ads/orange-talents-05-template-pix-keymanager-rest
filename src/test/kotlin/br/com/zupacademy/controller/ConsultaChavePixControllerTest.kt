package br.com.zupacademy.controller

import br.com.zupacademy.grpc.*
import br.com.zupacademy.model.request.RemoveChavePixRequest
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
class ConsultaChavePixControllerTest(
    private val grpcClient: KeymanagerConsultaGRPCServiceGrpc.KeymanagerConsultaGRPCServiceBlockingStub
) {
    @field:Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    lateinit var CHAVE_EXISTENTE: ConsultaChavePixResponseGRPC
    val RANDOM_CLIENT_ID = UUID.randomUUID().toString()
    val RANDOM_PIX_ID = UUID.randomUUID().toString()

    @BeforeEach
    fun setUp() {
        CHAVE_EXISTENTE = criaResponseGrpc()
    }

    @Test
    fun `deve consultar chave pix`() {
        // Cenário
        whenever(grpcClient.consultaGRPC(ConsultaChavePixRequestGRPC.newBuilder()
            .setPixId(
                ConsultaChavePixRequestGRPC.FiltroPixId.newBuilder()
                    .setClienteId(CHAVE_EXISTENTE.clienteId)
                    .setPixId(CHAVE_EXISTENTE.pixId)
                    .build()
            )
            .build())
        ).thenReturn(criaResponseGrpc())

        // Executa
        val request = HttpRequest.GET<Any>("/api/v1/pix/${CHAVE_EXISTENTE.clienteId}/${CHAVE_EXISTENTE.pixId}")
        val response = httpClient.toBlocking()
            .exchange(request, ConsultaChavePixResponse::class.java)

        // Valida
        with(response.body.get()) {
            assertThat(this.pixId, equalTo(CHAVE_EXISTENTE.pixId))
        }
    }

    @Test
    fun `deve informar chave pix nao encontrada`() {
        // Cenário
        val status = Status.fromCode(Status.NOT_FOUND.code)
            .withDescription("Chave Pix não encontrada")

        whenever(grpcClient.consultaGRPC(ConsultaChavePixRequestGRPC.newBuilder()
            .setPixId(
                ConsultaChavePixRequestGRPC.FiltroPixId.newBuilder()
                    .setClienteId(RANDOM_CLIENT_ID)
                    .setPixId(RANDOM_PIX_ID)
                    .build()
            )
            .build())
        ).thenThrow(StatusRuntimeException(status))

        // Executa
        val request = HttpRequest.GET<Any>("/api/v1/pix/$RANDOM_CLIENT_ID/$RANDOM_PIX_ID")

        val thrown = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, ConsultaChavePixResponse::class.java)
        }

        // Valida
        assertThat(thrown.status, equalTo(HttpStatus.NOT_FOUND))
        assertThat(thrown.message, equalTo("Chave Pix não encontrada"))
    }

    @Test
    fun `deve informar erro ao passar valores nao UUID`() {
        // Executa
        val request = HttpRequest.GET<Any>("/api/v1/pix/aaa/aaa")

        val thrown = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Any::class.java)
        }

        // Valida
        assertThat(thrown.status, equalTo(HttpStatus.BAD_REQUEST))
        assertThat(thrown.message, equalTo("Chave Pix não encontrada"))
    }

    private fun criaResponseGrpc():ConsultaChavePixResponseGRPC {
        return ConsultaChavePixResponseGRPC.newBuilder()
            .setClienteId("bc35591d-b547-4151-a325-4a9d2cd19614")
            .setPixId("80bde177-86f5-4f6e-aaea-559f3be19210")
            .setChave(ConsultaChavePixResponseGRPC.ChavePix.newBuilder()
                .setTipo(TipoDeChaveGRPC.ALEATORIA)
                .setChave("cf376a84-0ab1-49ad-9ee9-17336cec276a")
                .setConta(ConsultaChavePixResponseGRPC.ChavePix.ContaInfo.newBuilder()
                    .setTipo(TipoDeContaGRPC.CONTA_CORRENTE)
                    .setInstituicao("ITAÚ UNIBANCO S.A.")
                    .setNomeDoTitular("Tiago de Freitas")
                    .setCpfDoTitular("64370752019")
                    .setAgencia("0001")
                    .setNumeroDaConta("889976")
                    .build()
                )
                .setCriadaEm(LocalDateTime.now().let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
                .build()
            )
            .build()
    }

    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    class MockitoConsultaFactory {
        @Singleton
        fun consultaChaveStubGrpc(): KeymanagerConsultaGRPCServiceGrpc.KeymanagerConsultaGRPCServiceBlockingStub? {
            return Mockito.mock(KeymanagerConsultaGRPCServiceGrpc.KeymanagerConsultaGRPCServiceBlockingStub::class.java)
        }
    }

}

@Introspected
data class ConsultaChavePixResponse(
    val pixId: String?,
    val tipo: TipoDeChaveGRPC?,
    val chave: String?,
    val criadaEm: LocalDateTime?,
    val tipoConta: TipoDeContaGRPC?,
    val conta: ConsultaChavePixContaResponse?
)

@Introspected
data class ConsultaChavePixContaResponse(
    val instituicao: String?,
    val nomeDoTitular: String?,
    val cpfDoTitular: String?,
    val agencia: String?,
    val numero: String?
)
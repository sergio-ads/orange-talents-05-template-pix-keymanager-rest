package br.com.zupacademy.error

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

@MicronautTest
class GrpcExceptionHandlerTest {

    val requestGenerica = HttpRequest.GET<Any>("/")

    @Test
    fun `deve tratar erro de validacao INVALID_ARGUMENT via GRPC`() {
        // Cenário
        val exception = StatusRuntimeException(Status.INVALID_ARGUMENT)

        // Executa
        val resposta = GrpcExceptionHandler().handle(requestGenerica, exception)

        // Valida
        assertThat(resposta.status, equalTo(HttpStatus.BAD_REQUEST))
        assertThat(resposta.body(), notNullValue())
        assertThat(resposta.body()?.toString(), equalTo("Dados da requisição inválidos"))
    }

    @Test
    fun `deve tratar erro de validacao NOT_FOUND via GRPC`() {
        // Cenário
        val exception = StatusRuntimeException(Status.NOT_FOUND
            .withDescription("Não encontrado"))

        // Executa
        val resposta = GrpcExceptionHandler().handle(requestGenerica, exception)

        // Valida
        assertThat(resposta.status, equalTo(HttpStatus.NOT_FOUND))
        assertThat(resposta.body(), notNullValue())
        assertThat(resposta.body()?.toString(), equalTo("Não encontrado"))
    }

    @Test
    fun `deve tratar erro de validacao ALREADY_EXISTS via GRPC`() {
        // Cenário
        val exception = StatusRuntimeException(Status.ALREADY_EXISTS
            .withDescription("Já existe"))

        // Executa
        val resposta = GrpcExceptionHandler().handle(requestGenerica, exception)

        // Valida
        assertThat(resposta.status, equalTo(HttpStatus.UNPROCESSABLE_ENTITY))
        assertThat(resposta.body(), notNullValue())
        assertThat(resposta.body()?.toString(), equalTo("Já existe"))
    }

    @Test
    fun `deve tratar erro de validacao OTHER via GRPC`() {
        // Cenário
        val exception = StatusRuntimeException(Status.INTERNAL)

        // Executa
        val resposta = GrpcExceptionHandler().handle(requestGenerica, exception)

        // Valida
        assertThat(resposta.status, equalTo(HttpStatus.INTERNAL_SERVER_ERROR))
        assertThat(resposta.body(), notNullValue())
        assertThat(resposta.body()?.toString(), equalTo("Erro ao completar requisição:  (INTERNAL)"))
    }

}
package br.com.zupacademy.grpc

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class KeyManagerGrpcFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun registraChave() = KeymanagerRegistraGRPCServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removeChave() = KeymanagerRemoveGRPCServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listaChave() = KeymanagerListaGRPCServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun consultaChave() = KeymanagerConsultaGRPCServiceGrpc.newBlockingStub(channel)

}
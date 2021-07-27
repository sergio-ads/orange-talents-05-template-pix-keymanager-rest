package br.com.zupacademy.model

import br.com.zupacademy.grpc.TipoDeChaveGRPC
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoDeChave(val tipoGrpc: TipoDeChaveGRPC) {

    CPF(TipoDeChaveGRPC.CPF) {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank())
                return false

            if (!chave.matches("[0-9]+".toRegex()))
                return false

            return CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },
    CELULAR(TipoDeChaveGRPC.CELULAR) {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank())
                return false

            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL(TipoDeChaveGRPC.EMAIL) {
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank())
                return false

            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },
    ALEATORIA(TipoDeChaveGRPC.ALEATORIA) {
        override fun valida(chave: String?) = chave.isNullOrBlank()
    };

    abstract fun valida(chave: String?): Boolean

}

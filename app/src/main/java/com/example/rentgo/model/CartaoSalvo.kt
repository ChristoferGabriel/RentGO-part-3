package com.example.rentgo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

<<<<<<< HEAD
/**
 * Cartão salvo para pagar mais rápido (tabela "cartoes").
 *
 * Por segurança NÃO guardamos o número completo nem o CVV: só os 4 últimos dígitos
 * (suficiente para identificar o cartão na tela). O CVV é digitado de novo a cada pagamento.
 */
=======
>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)
@Entity(tableName = "cartoes")
data class CartaoSalvo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
<<<<<<< HEAD
    /** Dono do cartão (id do Usuario logado). */
    val usuarioId: Int,
    /** Nome dado pelo usuário, ex.: "Cartão do trabalho". */
=======

    val usuarioId: Int,

>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)
    val apelido: String,
    val nomeTitular: String,
    val ultimosDigitos: String,
    val bandeira: String,
<<<<<<< HEAD
    /** MM/AA */
    val validade: String,
    /** Cartão que já vem selecionado na tela de Pagamento. */
    val padrao: Boolean = false
) {
    /** "Visa •••• 4242" */
=======

    val validade: String,

    val padrao: Boolean = false
) {

>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)
    val descricao: String
        get() = "$bandeira •••• $ultimosDigitos"
}

<<<<<<< HEAD
/** Bandeira estimada pelo primeiro dígito (simplificação para o trabalho). */
=======
>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)
fun descobrirBandeira(numero: String): String {
    val digitos = numero.filter { it.isDigit() }
    return when (digitos.firstOrNull()) {
        '4' -> "Visa"
        '5', '2' -> "Mastercard"
        '6' -> "Elo"
        '3' -> "Amex"
        else -> "Cartão"
    }
}

<<<<<<< HEAD
/** true se o MM/AA já passou (o cartão vale até o fim do mês de validade). */
=======
>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)
fun isCartaoVencido(validade: String): Boolean {
    val partes = validade.split("/")
    if (partes.size != 2) return true
    val mes = partes[0].toIntOrNull() ?: return true
    val ano = partes[1].toIntOrNull() ?: return true
    val agora = java.util.Calendar.getInstance()
    val anoAtual = agora.get(java.util.Calendar.YEAR) % 100
    val mesAtual = agora.get(java.util.Calendar.MONTH) + 1
    return ano < anoAtual || (ano == anoAtual && mes < mesAtual)
}

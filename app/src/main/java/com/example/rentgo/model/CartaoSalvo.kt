package com.example.rentgo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

<<<<<<< HEAD
=======
>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)
@Entity(tableName = "cartoes")
data class CartaoSalvo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
<<<<<<< HEAD
    val usuarioId: Int,
=======

    val usuarioId: Int,

>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)
    val apelido: String,
    val nomeTitular: String,
    val ultimosDigitos: String,
    val bandeira: String,
<<<<<<< HEAD
    val validade: String,
    val padrao: Boolean = false
) {
    
=======

    val validade: String,

    val padrao: Boolean = false
) {

>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)
    val descricao: String
        get() = "$bandeira •••• $ultimosDigitos"
}

<<<<<<< HEAD
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

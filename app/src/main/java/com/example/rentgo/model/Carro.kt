package com.example.rentgo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

enum class Categoria(val rotulo: String) {
    SEDA("Sedã"),
    SUV("SUV"),
    CAMIONETE("Camionete"),
    ELETRICO("Elétrico")
}

@Entity(tableName = "carros")
data class Carro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val categoria: Categoria,
    val precoDia: Double,
    val nota: Double,
    val emOferta: Boolean = false,
    val favorito: Boolean = false,
    val subtitulo: String = "",
    val cambio: String = "Automático",
    val arCondicionado: Boolean = true,
    val lugares: Int = 5,
    val descricao: String = ""
) {
    val subtituloParaExibir: String
        get() = subtitulo.ifBlank { "${categoria.rotulo} • Completo" }

    val descricaoParaExibir: String
        get() = descricao.ifBlank {
            "O $nome é da categoria ${categoria.rotulo}, tem $lugares lugares e câmbio ${cambio.lowercase()}."
        }
}

fun formatarPreco(valor: Double): String =
    if (valor % 1.0 == 0.0) "R$ ${valor.toInt()}"
    else "R$ " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valor)
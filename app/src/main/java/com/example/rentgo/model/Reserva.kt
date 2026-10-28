package com.example.rentgo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.util.Locale

data class DadosReserva(
    val nomeCarro: String,
    val periodo: String,
    val valorCentavos: Long
)

fun formatarMoeda(centavos: Long): String {
    val formato = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    return formato.format(centavos / 100.0)
}

@Entity(tableName = "reservas")
data class Reserva(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carroId: Int,
    val nomeCarro: String,
    val precoDia: Double,
    val dias: Int
) {
    val total: Double
        get() = precoDia * dias
}

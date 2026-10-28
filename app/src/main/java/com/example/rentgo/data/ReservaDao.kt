package com.example.rentgo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rentgo.model.Reserva

@Dao
interface ReservaDao {

    @Insert
    suspend fun inserir(reserva: Reserva)

    @Query("SELECT * FROM reservas ORDER BY id")
    suspend fun buscarTodos(): List<Reserva>

    @Update
    suspend fun atualizar(reserva: Reserva)

    @Delete
    suspend fun deletar(reserva: Reserva)

    @Query("SELECT * FROM reservas WHERE id = :id")
    suspend fun buscarPorId(id: Int): Reserva?
}

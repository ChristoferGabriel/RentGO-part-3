package com.example.rentgo.data.repository

import android.content.Context
import com.example.rentgo.data.AppDatabase
import com.example.rentgo.data.ReservaDao
import com.example.rentgo.model.Reserva
import kotlinx.coroutines.flow.Flow

class ReservasRepository(private val reservaDao: ReservaDao) {

    val reservas: Flow<List<Reserva>> = reservaDao.buscarTodos()

    suspend fun buscarPorId(id: Int): Reserva? = reservaDao.buscarPorId(id)

    suspend fun inserir(reserva: Reserva) = reservaDao.inserir(reserva)

    suspend fun atualizar(reserva: Reserva) = reservaDao.atualizar(reserva)

    suspend fun deletar(reserva: Reserva) = reservaDao.deletar(reserva)

    companion object {
        fun criar(context: Context): ReservasRepository =
            ReservasRepository(AppDatabase.getDatabase(context).reservaDao())
    }
}

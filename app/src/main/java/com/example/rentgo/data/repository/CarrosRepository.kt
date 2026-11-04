package com.example.rentgo.data.repository

import android.content.Context
import com.example.rentgo.data.AppDatabase
import com.example.rentgo.data.CarroDao
import com.example.rentgo.model.Carro
import kotlinx.coroutines.flow.Flow

class CarrosRepository(private val carroDao: CarroDao) {

    val carros: Flow<List<Carro>> = carroDao.buscarTodos()

    suspend fun buscarPorId(id: Int): Carro? = carroDao.buscarPorId(id)

    suspend fun inserir(carro: Carro) = carroDao.inserir(carro)

    suspend fun atualizar(carro: Carro) = carroDao.atualizar(carro)

    suspend fun deletar(carro: Carro) = carroDao.deletar(carro)

    companion object {
        fun criar(context: Context): CarrosRepository =
            CarrosRepository(AppDatabase.getDatabase(context).carroDao())
    }
}

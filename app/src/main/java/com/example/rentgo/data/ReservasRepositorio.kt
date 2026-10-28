package com.example.rentgo.data

import android.content.Context
import android.util.Log
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Reserva
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ReservasRepositorio {

    private const val TAG = "ReservasRepositorio"

    private fun dao(context: Context): ReservaDao = AppDatabase.getDatabase(context).reservaDao()

    suspend fun buscarTodas(context: Context): List<Reserva> = withContext(Dispatchers.IO) {
        try {
            dao(context).buscarTodos()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar as reservas", e)
            emptyList()
        }
    }

    suspend fun buscarPorId(context: Context, id: Int): Reserva? = withContext(Dispatchers.IO) {
        try {
            dao(context).buscarPorId(id)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar a reserva de id $id", e)
            null
        }
    }

    fun inserir(context: Context, carro: Carro, dias: Int, aoTerminar: (List<Reserva>) -> Unit = {}) {
        val nova = Reserva(
            carroId = carro.id,
            nomeCarro = carro.nome,
            precoDia = carro.precoDia,
            dias = dias
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = dao(context)
                dao.inserir(nova)
                val listaAtualizada = dao.buscarTodos()
                withContext(Dispatchers.Main) { aoTerminar(listaAtualizada) }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao inserir a reserva do carro ${carro.nome}", e)
            }
        }
    }

    fun atualizar(context: Context, reserva: Reserva, aoTerminar: (List<Reserva>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = dao(context)
                dao.atualizar(reserva)
                val listaAtualizada = dao.buscarTodos()
                withContext(Dispatchers.Main) { aoTerminar(listaAtualizada) }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao atualizar a reserva ${reserva.id}", e)
            }
        }
    }

    fun deletar(context: Context, reserva: Reserva, aoTerminar: (List<Reserva>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = dao(context)
                dao.deletar(reserva)
                val listaAtualizada = dao.buscarTodos()
                withContext(Dispatchers.Main) { aoTerminar(listaAtualizada) }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao deletar a reserva ${reserva.id}", e)
            }
        }
    }
}

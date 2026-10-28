package com.example.rentgo.data

import android.content.Context
import android.util.Log
import com.example.rentgo.model.Carro
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CarrosRepositorio {

    private const val TAG = "CarrosRepositorio"

    // Quantos carros aparecem em "Ofertas da Semana": os mais baratos da lista de carros
    const val QUANTIDADE_OFERTAS = 4

    private fun dao(context: Context): CarroDao = AppDatabase.getDatabase(context).carroDao()

    suspend fun buscarTodos(context: Context): List<Carro> = withContext(Dispatchers.IO) {
        try {
            dao(context).buscarTodos()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar os carros", e)
            emptyList()
        }
    }

    suspend fun buscarOfertas(
        context: Context,
        quantidade: Int = QUANTIDADE_OFERTAS
    ): List<Carro> = withContext(Dispatchers.IO) {
        try {
            dao(context).buscarMaisBaratos(quantidade)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar as ofertas", e)
            emptyList()
        }
    }

    suspend fun buscarFavoritos(context: Context): List<Carro> = withContext(Dispatchers.IO) {
        try {
            dao(context).buscarFavoritos()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar os favoritos", e)
            emptyList()
        }
    }

    suspend fun buscarPorId(context: Context, id: Int): Carro? = withContext(Dispatchers.IO) {
        try {
            dao(context).buscarPorId(id)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar o carro de id $id", e)
            null
        }
    }

    fun inserir(context: Context, carro: Carro, aoTerminar: (List<Carro>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = dao(context)
                dao.inserir(carro)
                val listaAtualizada = dao.buscarTodos()
                withContext(Dispatchers.Main) { aoTerminar(listaAtualizada) }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao inserir o carro ${carro.nome}", e)
            }
        }
    }

    fun atualizar(context: Context, carro: Carro, aoTerminar: (List<Carro>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = dao(context)
                dao.atualizar(carro)
                val listaAtualizada = dao.buscarTodos()
                withContext(Dispatchers.Main) { aoTerminar(listaAtualizada) }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao atualizar o carro ${carro.nome}", e)
            }
        }
    }

    fun deletar(context: Context, carro: Carro, aoTerminar: (List<Carro>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = dao(context)
                dao.deletar(carro)
                val listaAtualizada = dao.buscarTodos()
                withContext(Dispatchers.Main) { aoTerminar(listaAtualizada) }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao deletar o carro ${carro.nome}", e)
            }
        }
    }
}
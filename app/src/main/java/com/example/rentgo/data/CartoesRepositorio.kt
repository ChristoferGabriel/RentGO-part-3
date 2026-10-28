package com.example.rentgo.data

import android.content.Context
import android.util.Log
import com.example.rentgo.model.CartaoSalvo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Cartões salvos no Room. Mesmo padrão dos outros repositórios do app:
 * leituras são `suspend`, escritas rodam em CoroutineScope(Dispatchers.IO).launch
 * e devolvem a lista atualizada em `aoTerminar` (já na thread principal).
 */
object CartoesRepositorio {

    private const val TAG = "CartoesRepositorio"

    private fun dao(context: Context): CartaoDao = AppDatabase.getDatabase(context).cartaoDao()

    /** Id do usuário logado (0 se ninguém estiver logado). */
    fun usuarioAtualId(): Int = SessaoUsuario.usuarioLogado?.id ?: 0

    suspend fun buscarDoUsuario(context: Context, usuarioId: Int): List<CartaoSalvo> =
        withContext(Dispatchers.IO) {
            try {
                dao(context).buscarPorUsuario(usuarioId)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao buscar os cartões do usuário $usuarioId", e)
                emptyList()
            }
        }

    suspend fun buscarPorId(context: Context, id: Int): CartaoSalvo? = withContext(Dispatchers.IO) {
        try {
            dao(context).buscarPorId(id)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar o cartão de id $id", e)
            null
        }
    }

    /**
     * Salva um cartão novo. O primeiro cartão do usuário vira o padrão automaticamente.
     * Se o mesmo cartão (mesmos 4 últimos dígitos, validade e titular) já estiver salvo, não duplica.
     */
    fun inserir(context: Context, cartao: CartaoSalvo, aoTerminar: (List<CartaoSalvo>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            var lista: List<CartaoSalvo> = emptyList()
            try {
                val dao = dao(context)
                val existentes = dao.buscarPorUsuario(cartao.usuarioId)
                val repetido = existentes.any {
                    it.ultimosDigitos == cartao.ultimosDigitos &&
                        it.validade == cartao.validade &&
                        it.nomeTitular == cartao.nomeTitular
                }
                if (!repetido) {
                    val tornarPadrao = cartao.padrao || existentes.isEmpty()
                    if (tornarPadrao) dao.limparPadrao(cartao.usuarioId)
                    dao.inserir(cartao.copy(padrao = tornarPadrao))
                }
                lista = dao.buscarPorUsuario(cartao.usuarioId)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao salvar o cartão ${cartao.descricao}", e)
            }
            withContext(Dispatchers.Main) { aoTerminar(lista) }
        }
    }

    fun atualizar(context: Context, cartao: CartaoSalvo, aoTerminar: (List<CartaoSalvo>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            var lista: List<CartaoSalvo> = emptyList()
            try {
                val dao = dao(context)
                if (cartao.padrao) dao.limparPadrao(cartao.usuarioId)
                dao.atualizar(cartao)
                lista = dao.buscarPorUsuario(cartao.usuarioId)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao atualizar o cartão ${cartao.id}", e)
            }
            withContext(Dispatchers.Main) { aoTerminar(lista) }
        }
    }

    /** Remove o cartão; se ele era o padrão, o próximo da lista passa a ser. */
    fun deletar(context: Context, cartao: CartaoSalvo, aoTerminar: (List<CartaoSalvo>) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            var lista: List<CartaoSalvo> = emptyList()
            try {
                val dao = dao(context)
                dao.deletar(cartao)
                lista = dao.buscarPorUsuario(cartao.usuarioId)
                if (cartao.padrao && lista.isNotEmpty()) {
                    dao.atualizar(lista.first().copy(padrao = true))
                    lista = dao.buscarPorUsuario(cartao.usuarioId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao deletar o cartão ${cartao.id}", e)
            }
            withContext(Dispatchers.Main) { aoTerminar(lista) }
        }
    }
}

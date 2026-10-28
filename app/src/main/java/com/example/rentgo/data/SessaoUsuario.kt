package com.example.rentgo.data

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.rentgo.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SessaoUsuario {

    private const val TAG = "SessaoUsuario"

    var usuarioLogado by mutableStateOf<Usuario?>(null)
        private set

    private fun dao(context: Context): UsuarioDao = AppDatabase.getDatabase(context).usuarioDao()

    fun cadastrar(
        context: Context,
        nome: String,
        email: String,
        senha: String,
        aoTerminar: (erro: String?) -> Unit
    ) {
        val emailLimpo = email.trim().lowercase()
        CoroutineScope(Dispatchers.IO).launch {
            var erro: String? = null
            var novoUsuario: Usuario? = null
            try {
                val dao = dao(context)
                if (dao.buscarPorEmail(emailLimpo) != null) {
                    erro = "Já existe uma conta com este e-mail."
                } else {
                    dao.inserir(Usuario(nome = nome.trim(), email = emailLimpo, senha = senha))
                    novoUsuario = dao.buscarPorEmail(emailLimpo)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao cadastrar a conta $emailLimpo", e)
                erro = "Não foi possível criar a conta. Tente de novo."
            }
            withContext(Dispatchers.Main) {
                if (novoUsuario != null) usuarioLogado = novoUsuario
                aoTerminar(erro)
            }
        }
    }

    fun entrar(context: Context, email: String, senha: String, aoTerminar: (deuCerto: Boolean) -> Unit) {
        val emailLimpo = email.trim().lowercase()
        CoroutineScope(Dispatchers.IO).launch {
            val encontrado: Usuario? = try {
                dao(context).buscarPorEmail(emailLimpo)?.takeIf { it.senha == senha }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao entrar com $emailLimpo", e)
                null
            }
            withContext(Dispatchers.Main) {
                if (encontrado != null) usuarioLogado = encontrado
                aoTerminar(encontrado != null)
            }
        }
    }

    fun redefinirSenha(context: Context, email: String, novaSenha: String, aoTerminar: (deuCerto: Boolean) -> Unit) {
        val emailLimpo = email.trim().lowercase()
        CoroutineScope(Dispatchers.IO).launch {
            val deuCerto = try {
                val dao = dao(context)
                val conta = dao.buscarPorEmail(emailLimpo)
                if (conta == null) {
                    false
                } else {
                    dao.atualizar(conta.copy(senha = novaSenha))
                    true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao redefinir a senha de $emailLimpo", e)
                false
            }
            withContext(Dispatchers.Main) { aoTerminar(deuCerto) }
        }
    }

    fun sair() {
        usuarioLogado = null
    }
}

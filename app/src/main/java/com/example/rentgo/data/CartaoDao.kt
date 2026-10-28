package com.example.rentgo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rentgo.model.CartaoSalvo

@Dao
interface CartaoDao {

    @Insert
    suspend fun inserir(cartao: CartaoSalvo)

    @Query("SELECT * FROM cartoes ORDER BY padrao DESC, id")
    suspend fun buscarTodos(): List<CartaoSalvo>

    @Update
    suspend fun atualizar(cartao: CartaoSalvo)

    @Delete
    suspend fun deletar(cartao: CartaoSalvo)

    @Query("SELECT * FROM cartoes WHERE id = :id")
    suspend fun buscarPorId(id: Int): CartaoSalvo?

    @Query("SELECT * FROM cartoes WHERE usuarioId = :usuarioId ORDER BY padrao DESC, id")
    suspend fun buscarPorUsuario(usuarioId: Int): List<CartaoSalvo>

    @Query("UPDATE cartoes SET padrao = 0 WHERE usuarioId = :usuarioId")
    suspend fun limparPadrao(usuarioId: Int)
}

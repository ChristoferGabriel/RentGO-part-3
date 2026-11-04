package com.example.rentgo.data
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rentgo.model.Carro
<<<<<<< HEAD
=======
import kotlinx.coroutines.flow.Flow
>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)

@Dao
interface CarroDao {

    @Insert
    suspend fun inserir(carro: Carro)

    @Query("SELECT * FROM carros ORDER BY id")
<<<<<<< HEAD
    suspend fun buscarTodos(): List<Carro>
=======
    fun buscarTodos(): Flow<List<Carro>>
>>>>>>> eeb93c3 (Atualizacao Arquitetura MVVM)

    @Update
    suspend fun atualizar(carro: Carro)

    @Delete
    suspend fun deletar(carro: Carro)

    @Query("SELECT * FROM carros WHERE id = :id")
    suspend fun buscarPorId(id: Int): Carro?

    @Query("SELECT * FROM carros ORDER BY precoDia ASC, id ASC LIMIT :limite")
    suspend fun buscarMaisBaratos(limite: Int): List<Carro>

    @Query("SELECT * FROM carros WHERE favorito = 1 ORDER BY id")
    suspend fun buscarFavoritos(): List<Carro>
}

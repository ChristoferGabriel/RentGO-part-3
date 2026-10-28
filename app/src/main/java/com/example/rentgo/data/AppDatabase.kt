package com.example.rentgo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rentgo.model.CartaoSalvo
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Categoria
import com.example.rentgo.model.Reserva
import com.example.rentgo.model.Usuario

@Database(
    entities = [Carro::class, Reserva::class, Usuario::class, CartaoSalvo::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carroDao(): CarroDao
    abstract fun reservaDao(): ReservaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun cartaoDao(): CartaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rentgo_database"
                )
                    .addMigrations(MIGRACAO_1_2)
                    .addCallback(CarrosIniciais)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

private object CarrosIniciais : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val carros = listOf(
            Carro(
                nome = "Ford Focus 2023", categoria = Categoria.SEDA, precoDia = 120.0, nota = 4.9, emOferta = true,
                subtitulo = "Hatchback • Completo",
                descricao = "O Ford Focus 2023 combina performance ágil, tecnologia inteligente e excelente consumo de combustível. Ideal tanto para o trânsito urbano quanto para viagens rápidas de fim de semana."
            ),
            Carro(
                nome = "Jeep Compass", categoria = Categoria.SUV, precoDia = 180.0, nota = 4.8, emOferta = true,
                subtitulo = "SUV • Completo",
                descricao = "O Jeep Compass une conforto, espaço interno de sobra e a robustez de um SUV. Perfeito para viagens em família e estradas de todo tipo."
            ),
            Carro(
                nome = "BYD Dolphin", categoria = Categoria.ELETRICO, precoDia = 210.0, nota = 4.9, emOferta = true,
                subtitulo = "Hatch elétrico • Completo",
                descricao = "O BYD Dolphin é 100% elétrico: silencioso, econômico e cheio de tecnologia. Ótimo para rodar pela cidade sem gastar com combustível."
            ),
            Carro(
                nome = "Toyota Corolla", categoria = Categoria.SEDA, precoDia = 150.0, nota = 4.7, emOferta = true,
                subtitulo = "Sedã • Completo",
                descricao = "O Toyota Corolla é sinônimo de conforto e confiabilidade. Porta-malas amplo e direção suave, tanto para o dia a dia quanto para viagens longas."
            ),
            Carro(
                nome = "Hyundai Creta", categoria = Categoria.SUV, precoDia = 165.0, nota = 4.6,
                subtitulo = "SUV compacto • Completo",
                descricao = "O Hyundai Creta tem posição de dirigir elevada, bom espaço interno e consumo equilibrado. Uma escolha versátil para cidade e estrada."
            ),
            Carro(
                nome = "Renault Kwid E-Tech", categoria = Categoria.ELETRICO, precoDia = 140.0, nota = 4.5,
                subtitulo = "Compacto elétrico • Básico", lugares = 4,
                descricao = "O Renault Kwid E-Tech é o elétrico mais compacto da frota: fácil de estacionar, econômico e ideal para trajetos curtos na cidade."
            )
        )
        carros.forEach { carro ->
            db.execSQL(
                "INSERT INTO carros (nome, categoria, precoDia, nota, emOferta, favorito, subtitulo, cambio, arCondicionado, lugares, descricao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                arrayOf<Any>(
                    carro.nome,
                    carro.categoria.name,
                    carro.precoDia,
                    carro.nota,
                    if (carro.emOferta) 1 else 0,
                    if (carro.favorito) 1 else 0,
                    carro.subtitulo,
                    carro.cambio,
                    if (carro.arCondicionado) 1 else 0,
                    carro.lugares,
                    carro.descricao
                )
            )
        }
    }
}

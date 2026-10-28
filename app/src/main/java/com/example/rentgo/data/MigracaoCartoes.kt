package com.example.rentgo.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Versão 1 -> 2 do banco: cria a tabela "cartoes" (cartões salvos do Pagamento)
 * sem apagar carros, reservas e usuários que já estão no aparelho.
 */
val MIGRACAO_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `cartoes` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`usuarioId` INTEGER NOT NULL, " +
                "`apelido` TEXT NOT NULL, " +
                "`nomeTitular` TEXT NOT NULL, " +
                "`ultimosDigitos` TEXT NOT NULL, " +
                "`bandeira` TEXT NOT NULL, " +
                "`validade` TEXT NOT NULL, " +
                "`padrao` INTEGER NOT NULL)"
        )
    }
}

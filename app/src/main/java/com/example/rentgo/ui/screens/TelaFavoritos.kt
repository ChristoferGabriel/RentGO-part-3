package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.model.Carro
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun TelaFavoritos(onAbrirCarro: (Int) -> Unit) {
    val context = LocalContext.current
    var favoritos by remember { mutableStateOf(listOf<Carro>()) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        favoritos = CarrosRepositorio.buscarFavoritos(context)
        carregando = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RentGoCores.Fundo)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Favoritos",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = RentGoCores.TextoEscuro,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (!carregando && favoritos.isEmpty()) {
            Text(
                text = "Nenhum favorito ainda.\nAbra um carro e toque no coração para salvar aqui.",
                color = RentGoCores.TextoCinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(favoritos, key = { it.id }) { carro ->
                CardDoCarro(
                    carro = carro,
                    aoAbrir = { onAbrirCarro(carro.id) },
                    aoRemover = {
                        CarrosRepositorio.atualizar(context, carro.copy(favorito = false)) { listaAtualizada ->
                            favoritos = listaAtualizada.filter { it.favorito }
                        }
                    },
                    iconeAcao = Icons.Filled.Favorite,
                    descricaoAcao = "Remover dos favoritos"
                )
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

package com.example.rentgo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import com.example.rentgo.data.ReservasRepositorio
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Categoria
import com.example.rentgo.model.Reserva
import com.example.rentgo.model.formatarPreco
import com.example.rentgo.ui.components.FotoCarro
import com.example.rentgo.ui.components.TopoRentGo
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun TelaDetalheReserva(reservaId: Int, onVoltar: () -> Unit) {
    val context = LocalContext.current
    var reservaDoBanco by remember(reservaId) { mutableStateOf<Reserva?>(null) }
    var carroDaReserva by remember(reservaId) { mutableStateOf<Carro?>(null) }
    var carregando by remember(reservaId) { mutableStateOf(true) }
    var cancelando by remember { mutableStateOf(false) }

    LaunchedEffect(reservaId) {
        val encontrada = ReservasRepositorio.buscarPorId(context, reservaId)
        reservaDoBanco = encontrada
        carroDaReserva = if (encontrada != null) CarrosRepositorio.buscarPorId(context, encontrada.carroId) else null
        carregando = false
    }

    Scaffold(
        containerColor = RentGoCores.Fundo,
        topBar = { TopoRentGo(titulo = "Detalhe da reserva", onVoltar = onVoltar) }
    ) { paddingInterno ->
        val reserva = reservaDoBanco
        if (reserva == null) {
            Text(
                text = if (carregando) "Carregando..." else "Reserva não encontrada (talvez já tenha sido cancelada).",
                color = RentGoCores.TextoCinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno)
                    .padding(32.dp)
            )
            return@Scaffold
        }
        val carro = carroDaReserva
        val carroParaFoto = carro ?: Carro(
            id = reserva.carroId,
            nome = reserva.nomeCarro,
            categoria = Categoria.SEDA,
            precoDia = reserva.precoDia,
            nota = 0.0
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                FotoCarro(
                    carro = carroParaFoto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                )
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = reserva.nomeCarro,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.TextoEscuro
                    )
                    Spacer(Modifier.height(6.dp))
                    if (carro != null) {
                        Text("${carro.categoria.rotulo} · ★ ${carro.nota}", fontSize = 14.sp, color = RentGoCores.TextoCinza)
                    } else {
                        Text("Esse carro foi removido da lista de carros.", fontSize = 14.sp, color = RentGoCores.TextoCinza)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "${formatarPreco(reserva.precoDia)} / dia",
                        fontSize = 15.sp,
                        color = RentGoCores.TextoEscuro
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Resumo da reserva", fontWeight = FontWeight.Bold, color = RentGoCores.TextoEscuro)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (reserva.dias == 1) "1 diária" else "${reserva.dias} diárias",
                        fontSize = 15.sp,
                        color = RentGoCores.TextoEscuro
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Total: ${formatarPreco(arredondar(reserva.total))}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.Azul
                    )
                    Text(
                        text = "${reserva.dias} × ${formatarPreco(reserva.precoDia)}",
                        fontSize = 13.sp,
                        color = RentGoCores.TextoCinza
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    cancelando = true
                    ReservasRepositorio.deletar(context, reserva) { onVoltar() }
                },
                enabled = !cancelando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, RentGoCores.Vermelho),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RentGoCores.Vermelho)
            ) {
                Text("Cancelar reserva", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
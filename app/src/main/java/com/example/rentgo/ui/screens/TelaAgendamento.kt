package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.data.ReservasRepositorio
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Reserva
import com.example.rentgo.model.formatarPreco
import com.example.rentgo.ui.components.MAX_DIARIAS
import com.example.rentgo.ui.components.MIN_DIARIAS
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun TelaAgendamento(onAbrirReserva: (Int) -> Unit) {
    val context = LocalContext.current
    var reservas by remember { mutableStateOf(listOf<Reserva>()) }
    var carregando by remember { mutableStateOf(true) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        reservas = ReservasRepositorio.buscarTodas(context)
        carregando = false
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RentGoCores.Fundo)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Minhas reservas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = RentGoCores.TextoEscuro,
                modifier = Modifier.weight(1f)
            )
        }

        if (!carregando && reservas.isEmpty()) {
            Text(
                text = "Você ainda não tem reservas.\nToque em \"Agendar\" ou reserve um carro na aba Carros.",
                color = RentGoCores.TextoCinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(reservas, key = { it.id }) { reserva ->
                CardDaReserva(
                    reserva = reserva,
                    aoAbrir = { onAbrirReserva(reserva.id) },
                    aoRemover = {
                        ReservasRepositorio.deletar(context, reserva) { listaAtualizada -> reservas = listaAtualizada }
                    }
                )
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun CardDaReserva(reserva: Reserva, aoAbrir: () -> Unit, aoRemover: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = aoAbrir),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reserva.nomeCarro,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.TextoEscuro
                )
                Text(
                    text = "${reserva.dias} ${if (reserva.dias == 1) "diária" else "diárias"}",
                    fontSize = 12.sp,
                    color = RentGoCores.TextoCinza
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Total: ${formatarPreco(reserva.total)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.TextoEscuro
                )
            }
            IconButton(onClick = aoRemover) {
                Icon(Icons.Outlined.Delete, contentDescription = "Cancelar reserva", tint = RentGoCores.Vermelho)
            }
        }
    }
}

@Composable
private fun DialogoNovaReserva(onFechar: () -> Unit, onSalvar: (Carro, Int) -> Unit) {
    val context = LocalContext.current
    var carrosDisponiveis by remember { mutableStateOf(listOf<Carro>()) }
    var carroEscolhido by remember { mutableStateOf<Carro?>(null) }
    var menuAberto by remember { mutableStateOf(false) }
    var diasTexto by remember { mutableStateOf("1") }
    var erro by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        carrosDisponiveis = CarrosRepositorio.buscarTodos(context)
    }

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Nova reserva", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Box {
                    OutlinedButton(
                        onClick = { menuAberto = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(carroEscolhido?.nome ?: "Escolha o carro")
                    }
                    DropdownMenu(expanded = menuAberto, onDismissRequest = { menuAberto = false }) {
                        carrosDisponiveis.forEach { carro ->
                            DropdownMenuItem(
                                text = { Text("${carro.nome} · ${formatarPreco(carro.precoDia)}/dia") },
                                onClick = {
                                    carroEscolhido = carro
                                    erro = null
                                    menuAberto = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = diasTexto,
                    onValueChange = { diasTexto = it.filter(Char::isDigit); erro = null },
                    label = { Text("Número de diárias") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                erro?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = RentGoCores.Vermelho, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val dias = diasTexto.toIntOrNull()
                val carro = carroEscolhido
                when {
                    carro == null -> erro = "Escolha um carro."
                    dias == null || dias !in MIN_DIARIAS..MAX_DIARIAS ->
                        erro = "Informe de $MIN_DIARIAS a $MAX_DIARIAS diárias."
                    else -> onSalvar(carro, dias)
                }
            }) { Text("Agendar") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancelar") }
        }
    )
}

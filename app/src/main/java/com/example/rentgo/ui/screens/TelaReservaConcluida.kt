package com.example.rentgo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.model.DadosReserva
import com.example.rentgo.model.formatarMoeda
import com.example.rentgo.ui.components.PaymentTopBar
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun TelaReservaConcluida(
    reserva: DadosReserva,
    valorPagoCentavos: Long,
    onNovaReserva: () -> Unit,
    formaPagamento: String = ""
) {
    Scaffold(
        topBar = { PaymentTopBar(titulo = "Reserva", comVoltar = false) },
        containerColor = RentGoCores.Fundo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RentGoCores.Fundo)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RentGoCores.Azul),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        text = "Reserva Concluída",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "No valor de ${formatarMoeda(valorPagoCentavos)}",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    if (formaPagamento.isNotBlank()) {
                        Text(
                            text = "Pago com $formaPagamento",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color.White.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = reserva.nomeCarro,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = reserva.periodo,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            IconeCheck(modifier = Modifier.size(96.dp))

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNovaReserva,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RentGoCores.Azul)
            ) {
                Text(
                    text = "NOVA RESERVA",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun IconeCheck(modifier: Modifier = Modifier, cor: Color = RentGoCores.Verde) {
    Canvas(modifier = modifier) {
        val espessura = size.width * 0.09f
        val inicio = Offset(size.width * 0.12f, size.height * 0.55f)
        val vertice = Offset(size.width * 0.40f, size.height * 0.82f)
        val fim = Offset(size.width * 0.88f, size.height * 0.20f)

        drawLine(
            color = cor,
            start = inicio,
            end = vertice,
            strokeWidth = espessura,
            cap = StrokeCap.Round
        )
        drawLine(
            color = cor,
            start = vertice,
            end = fim,
            strokeWidth = espessura,
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TelaReservaConcluidaPreview() {
    TelaReservaConcluida(
        reserva = DadosReserva("Ford Focus 2023", "15 Out - 20 Out (5 Diárias)", 60_000L),
        valorPagoCentavos = 57_000L,
        onNovaReserva = {}
    )
}

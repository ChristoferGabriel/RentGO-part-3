package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.model.Carro
import com.example.rentgo.model.formatarPreco
import com.example.rentgo.ui.components.FotoCarro
import com.example.rentgo.ui.components.MAX_DIARIAS
import com.example.rentgo.ui.components.MIN_DIARIAS
import com.example.rentgo.ui.components.TopoRentGo
import com.example.rentgo.ui.theme.RentGoCores

private const val DIARIAS_SUGERIDAS = 5
private val atalhosDeDiarias = listOf(1, 3, 5, 7, 15, 30)

private fun textoDiarias(dias: Int): String = if (dias == 1) "1 diária" else "$dias diárias"

@Composable
fun TelaEscolherDiarias(
    carroId: Int,
    onVoltar: () -> Unit,
    onContinuar: (Carro, Int) -> Unit
) {
    val context = LocalContext.current
    var carroDoBanco by remember(carroId) { mutableStateOf<Carro?>(null) }
    var carregando by remember(carroId) { mutableStateOf(true) }
    var dias by rememberSaveable { mutableIntStateOf(DIARIAS_SUGERIDAS) }

    LaunchedEffect(carroId) {
        carroDoBanco = CarrosRepositorio.buscarPorId(context, carroId)
        carregando = false
    }

    val carro = carroDoBanco

    Scaffold(
        containerColor = RentGoCores.Fundo,
        topBar = { TopoRentGo(titulo = "Quantas diárias?", onVoltar = onVoltar) },
        bottomBar = {
            if (carro != null) {
                BarraTotalContinuar(
                    total = arredondar(carro.precoDia * dias),
                    dias = dias,
                    onContinuar = { onContinuar(carro, dias) }
                )
            }
        }
    ) { paddingInterno ->
        if (carro == null) {
            Text(
                text = if (carregando) "Carregando..." else "Esse carro não existe mais (foi removido da lista).",
                color = RentGoCores.TextoCinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno)
                    .padding(32.dp)
            )
        } else {
            val total = arredondar(carro.precoDia * dias)
            val totalPix = arredondar(total * 0.95)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                ResumoDoCarro(carro = carro)

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Por quantos dias você vai ficar com o carro?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RentGoCores.TextoEscuro,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                        Spacer(Modifier.height(18.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BotaoContador(
                                icone = Icons.Outlined.Remove,
                                descricao = "Menos uma diária",
                                habilitado = dias > MIN_DIARIAS,
                                onClick = { dias = (dias - 1).coerceIn(MIN_DIARIAS, MAX_DIARIAS) }
                            )
                            Column(
                                modifier = Modifier.width(120.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$dias",
                                    fontSize = 44.sp,
                                    lineHeight = 48.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RentGoCores.TextoEscuro
                                )
                                Text(
                                    text = if (dias == 1) "diária" else "diárias",
                                    fontSize = 14.sp,
                                    color = RentGoCores.TextoCinza
                                )
                            }
                            BotaoContador(
                                icone = Icons.Outlined.Add,
                                descricao = "Mais uma diária",
                                habilitado = dias < MAX_DIARIAS,
                                onClick = { dias = (dias + 1).coerceIn(MIN_DIARIAS, MAX_DIARIAS) }
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            atalhosDeDiarias.forEach { opcao ->
                                AtalhoDiarias(
                                    texto = if (opcao == 1) "1 dia" else "$opcao dias",
                                    marcado = opcao == dias,
                                    onClick = { dias = opcao }
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "De $MIN_DIARIAS a $MAX_DIARIAS diárias",
                            fontSize = 12.sp,
                            color = RentGoCores.TextoCinza
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
                    Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
                        LinhaValor(
                            rotulo = "${formatarPreco(carro.precoDia)} × ${textoDiarias(dias)}",
                            valor = formatarPreco(total)
                        )
                        LinhaValor(
                            rotulo = "Pagando no Pix (5% de desconto)",
                            valor = formatarPreco(totalPix),
                            corValor = RentGoCores.Verde
                        )
                        HorizontalDivider(
                            color = RentGoCores.Borda,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        LinhaValor(
                            rotulo = "Total",
                            valor = formatarPreco(total),
                            destaque = true
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ResumoDoCarro(carro: Carro) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FotoCarro(
                carro = carro,
                modifier = Modifier
                    .width(96.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                tamanhoIcone = 36.dp
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = carro.nome,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.TextoEscuro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = carro.subtituloParaExibir,
                    fontSize = 13.sp,
                    color = RentGoCores.TextoCinza,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${formatarPreco(carro.precoDia)} / dia",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RentGoCores.Azul
                )
            }
        }
    }
}

@Composable
private fun BotaoContador(
    icone: ImageVector,
    descricao: String,
    habilitado: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(if (habilitado) RentGoCores.AzulClaro else RentGoCores.Fundo)
            .clickable(enabled = habilitado, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icone,
            contentDescription = descricao,
            tint = if (habilitado) RentGoCores.Azul else RentGoCores.Placeholder,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun AtalhoDiarias(texto: String, marcado: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .clip(forma)
            .background(if (marcado) RentGoCores.Azul else RentGoCores.Branco)
            .border(1.dp, if (marcado) RentGoCores.Azul else RentGoCores.Borda, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = texto,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (marcado) RentGoCores.Branco else RentGoCores.TextoEscuro
        )
    }
}

@Composable
private fun LinhaValor(
    rotulo: String,
    valor: String,
    destaque: Boolean = false,
    corValor: Color = RentGoCores.TextoEscuro
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rotulo,
            fontSize = if (destaque) 17.sp else 14.sp,
            fontWeight = if (destaque) FontWeight.Bold else FontWeight.Normal,
            color = if (destaque) RentGoCores.TextoEscuro else RentGoCores.TextoCinza,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            fontSize = if (destaque) 20.sp else 15.sp,
            fontWeight = if (destaque) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (destaque) RentGoCores.Azul else corValor
        )
    }
}

@Composable
private fun BarraTotalContinuar(total: Double, dias: Int, onContinuar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RentGoCores.Branco)
            .navigationBarsPadding()
    ) {
        HorizontalDivider(color = RentGoCores.Borda)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Total · ${textoDiarias(dias)}",
                    fontSize = 13.sp,
                    color = RentGoCores.TextoCinza
                )
                Text(
                    text = formatarPreco(total),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RentGoCores.TextoEscuro
                )
            }
            Button(
                onClick = onContinuar,
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RentGoCores.Azul,
                    contentColor = RentGoCores.Branco
                )
            ) {
                Text("CONTINUAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

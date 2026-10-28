package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.model.Carro
import com.example.rentgo.model.formatarPreco
import com.example.rentgo.ui.components.FotoCarro
import com.example.rentgo.ui.components.TopoRentGo
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun TelaDetalheCarro(
    carroId: Int,
    onVoltar: () -> Unit,
    onReservar: (Carro) -> Unit
) {
    val context = LocalContext.current
    var carroDoBanco by remember(carroId) { mutableStateOf<Carro?>(null) }
    var carregando by remember(carroId) { mutableStateOf(true) }
    var mostrarEdicao by remember { mutableStateOf(false) }

    LaunchedEffect(carroId) {
        carroDoBanco = CarrosRepositorio.buscarPorId(context, carroId)
        carregando = false
    }

    fun atualizarComBanco(listaAtualizada: List<Carro>) {
        carroDoBanco = listaAtualizada.find { it.id == carroId }
    }

    val carro = carroDoBanco
    if (carro == null) {
        Scaffold(
            containerColor = RentGoCores.Fundo,
            topBar = {
                TopoRentGo(
                    titulo = if (carregando) "Carregando..." else "Carro não encontrado",
                    onVoltar = onVoltar
                )
            }
        ) { paddingInterno ->
            Text(
                text = if (carregando) "Carregando..." else "Esse carro não existe mais (foi removido da lista).",
                color = RentGoCores.TextoCinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno)
                    .padding(32.dp)
            )
        }
    } else {
        if (mostrarEdicao) {
            DialogoCarro(
                carroParaEditar = carro,
                onFechar = { mostrarEdicao = false },
                onSalvar = { nome, categoria, preco, cambio, arCondicionado, lugares ->
                    val editado = carro.copy(
                        nome = nome.trim(),
                        categoria = categoria,
                        precoDia = preco,
                        cambio = cambio,
                        arCondicionado = arCondicionado,
                        lugares = lugares
                    )
                    CarrosRepositorio.atualizar(context, editado) { lista -> atualizarComBanco(lista) }
                    mostrarEdicao = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RentGoCores.Branco)
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.3f)
                ) {
                    FotoCarro(
                        carro = carro,
                        modifier = Modifier.fillMaxSize(),
                        tamanhoIcone = 96.dp
                    )
                    BarraSobreFoto(
                        favorito = carro.favorito,
                        onVoltar = onVoltar,
                        onEditar = { mostrarEdicao = true },
                        onFavoritar = {
                            CarrosRepositorio.atualizar(context, carro.copy(favorito = !carro.favorito)) { lista ->
                                atualizarComBanco(lista)
                            }
                        }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-28).dp)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(RentGoCores.Branco)
                        .padding(start = 24.dp, end = 24.dp, top = 30.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = carro.nome.uppercase(),
                                fontSize = 26.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RentGoCores.TextoEscuro
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = carro.subtituloParaExibir,
                                fontSize = 15.sp,
                                color = RentGoCores.TextoCinza
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        SeloNotaDetalhe(nota = carro.nota)
                    }

                    Spacer(Modifier.height(24.dp))
                    DivisorComAlca()
                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Especificações técnicas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.TextoEscuro
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CardEspecificacao(
                            icone = Icons.Outlined.Build,
                            texto = carro.cambio,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        CardEspecificacao(
                            icone = Icons.Outlined.AcUnit,
                            texto = if (carro.arCondicionado) "Ar condicionado" else "Sem ar",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        CardEspecificacao(
                            icone = Icons.Outlined.People,
                            texto = "${carro.lugares} Lugares",
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }

                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = "Sobre o veículo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.TextoEscuro
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = carro.descricaoParaExibir,
                        fontSize = 15.sp,
                        lineHeight = 23.sp,
                        color = RentGoCores.TextoCinza
                    )
                }
            }

            BarraPrecoReservar(
                precoDia = carro.precoDia,
                onReservar = { onReservar(carro) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraSobreFoto(
    favorito: Boolean,
    onVoltar: () -> Unit,
    onEditar: () -> Unit,
    onFavoritar: () -> Unit
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            BotaoSobreFoto(
                icone = Icons.AutoMirrored.Rounded.ArrowBack,
                descricao = "Voltar",
                onClick = onVoltar
            )
        },
        actions = {
            BotaoSobreFoto(
                icone = Icons.Outlined.Edit,
                descricao = "Editar carro",
                onClick = onEditar
            )
            BotaoSobreFoto(
                icone = if (favorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                descricao = if (favorito) "Remover dos favoritos" else "Favoritar",
                tint = if (favorito) RentGoCores.Vermelho else RentGoCores.TextoEscuro,
                onClick = onFavoritar
            )
        },
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun BotaoSobreFoto(
    icone: ImageVector,
    descricao: String,
    tint: Color = RentGoCores.TextoEscuro,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(42.dp)
            .clip(CircleShape)
            .background(RentGoCores.Branco.copy(alpha = 0.92f))
    ) {
        Icon(
            imageVector = icone,
            contentDescription = descricao,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun SeloNotaDetalhe(nota: Double) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(RentGoCores.AzulClaro)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.StarBorder,
            contentDescription = null,
            tint = RentGoCores.Azul,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = nota.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = RentGoCores.Azul
        )
    }
}

@Composable
private fun DivisorComAlca() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(11.dp)
    ) {
        HorizontalDivider(
            color = RentGoCores.Borda,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(130.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(RentGoCores.TextoEscuro)
        )
    }
}

@Composable
private fun CardEspecificacao(
    icone: ImageVector,
    texto: String,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .clip(forma)
            .background(RentGoCores.Fundo)
            .border(1.dp, RentGoCores.Borda, forma)
            .padding(horizontal = 6.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = RentGoCores.Azul,
            modifier = Modifier.size(30.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = texto,
            fontSize = 13.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = RentGoCores.TextoEscuro,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BarraPrecoReservar(precoDia: Double, onReservar: () -> Unit) {
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
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Preço por dia",
                    fontSize = 13.sp,
                    color = RentGoCores.TextoCinza
                )
                Row {
                    Text(
                        text = formatarPreco(precoDia),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RentGoCores.TextoEscuro,
                        modifier = Modifier.alignByBaseline()
                    )
                    Text(
                        text = "/ dia",
                        fontSize = 15.sp,
                        color = RentGoCores.TextoCinza,
                        modifier = Modifier
                            .alignByBaseline()
                            .padding(start = 3.dp)
                    )
                }
            }
            Button(
                onClick = onReservar,
                modifier = Modifier.height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RentGoCores.Azul,
                    contentColor = RentGoCores.Branco
                )
            ) {
                Text("RESERVAR AGORA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

internal fun arredondar(valor: Double): Double = Math.round(valor * 100) / 100.0
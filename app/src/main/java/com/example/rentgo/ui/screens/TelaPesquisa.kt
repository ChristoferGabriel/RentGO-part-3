package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Categoria
import com.example.rentgo.model.formatarPreco
import com.example.rentgo.ui.theme.RentGoCores

private val listaDeFiltros = listOf("Sedã", "SUV", "Camionete", "Elétrico", "Até R$ 150")

private val opcoesDeCambio = listOf("Automático", "Manual")
private const val MIN_LUGARES = 2
private const val MAX_LUGARES = 9

private fun filtrarCarros(carros: List<Carro>, busca: String, filtros: List<Boolean>): List<Carro> {
    val categoriasMarcadas = buildList {
        if (filtros[0]) add(Categoria.SEDA)
        if (filtros[1]) add(Categoria.SUV)
        if (filtros[2]) add(Categoria.CAMIONETE)
        if (filtros[3]) add(Categoria.ELETRICO)
    }
    return carros.filter { carro ->
        (busca.isBlank() || carro.nome.contains(busca.trim(), ignoreCase = true)) &&
                (categoriasMarcadas.isEmpty() || carro.categoria in categoriasMarcadas) &&
                (!filtros[4] || carro.precoDia <= 150.0)
    }
}

@Composable
fun TelaPesquisa(
    onVoltar: () -> Unit,
    onAbrirCarro: (Carro) -> Unit
) {
    val context = LocalContext.current
    var busca by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var carroEmEdicao by remember { mutableStateOf<Carro?>(null) }

    var carrosDoBanco by remember { mutableStateOf(listOf<Carro>()) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        carrosDoBanco = CarrosRepositorio.buscarTodos(context)
        carregando = false
    }

    val filtrosSelecionados = remember { mutableStateListOf(false, false, false, false, false) }
    val carros = filtrarCarros(carrosDoBanco, busca, filtrosSelecionados)

    if (mostrarDialogo) {
        DialogoCarro(
            carroParaEditar = carroEmEdicao,
            onFechar = { mostrarDialogo = false },
            onSalvar = { nome, categoria, preco, cambio, arCondicionado, lugares ->
                val editando = carroEmEdicao
                if (editando == null) {
                    val novo = Carro(
                        nome = nome.trim(),
                        categoria = categoria,
                        precoDia = preco,
                        nota = 5.0,
                        cambio = cambio,
                        arCondicionado = arCondicionado,
                        lugares = lugares
                    )
                    CarrosRepositorio.inserir(context, novo) { listaAtualizada -> carrosDoBanco = listaAtualizada }
                } else {
                    val editado = editando.copy(
                        nome = nome.trim(),
                        categoria = categoria,
                        precoDia = preco,
                        cambio = cambio,
                        arCondicionado = arCondicionado,
                        lugares = lugares
                    )
                    CarrosRepositorio.atualizar(context, editado) { listaAtualizada -> carrosDoBanco = listaAtualizada }
                }
                mostrarDialogo = false
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = RentGoCores.Fundo
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopoPesquisa(
                onVoltar = onVoltar,
                onLimpar = {
                    busca = ""
                    for (i in filtrosSelecionados.indices) filtrosSelecionados[i] = false
                }
            )

            LinhaLocalidadeEData(
                localidade = busca,
                aoMudarLocalidade = { busca = it }
            )

            LinhaDeFiltros(
                filtros = listaDeFiltros,
                selecionados = filtrosSelecionados,
                aoClicarFiltro = { indice ->
                    filtrosSelecionados[indice] = !filtrosSelecionados[indice]
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (carregando) "Carregando..." else "${carros.size} veículos encontrados",
                    fontSize = 13.sp,
                    color = RentGoCores.TextoCinza,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        carroEmEdicao = null
                        mostrarDialogo = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RentGoCores.Azul,
                        contentColor = RentGoCores.Branco
                    )
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Adicionar carro", fontSize = 13.sp)
                }
            }

            if (!carregando && carros.isEmpty()) {
                Text(
                    text = "Nenhum carro encontrado. Limpe os filtros (↻) ou adicione um carro.",
                    fontSize = 14.sp,
                    color = RentGoCores.TextoCinza,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                items(carros, key = { it.id }) { carro ->
                    CardDoCarro(
                        carro = carro,
                        aoAbrir = { onAbrirCarro(carro) },
                        aoEditar = {
                            carroEmEdicao = carro
                            mostrarDialogo = true
                        },
                        aoRemover = {
                            CarrosRepositorio.deletar(context, carro) { listaAtualizada -> carrosDoBanco = listaAtualizada }
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

private fun textoDoPreco(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toInt().toString() else valor.toString()

@Composable
private fun OpcaoChip(texto: String, marcada: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (marcada) RentGoCores.Azul else RentGoCores.AzulClaro)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = texto,
            fontSize = 13.sp,
            color = if (marcada) RentGoCores.Branco else RentGoCores.Azul
        )
    }
}

@Composable
private fun BotaoQuantidade(simbolo: String, habilitado: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(RentGoCores.AzulClaro)
            .clickable(enabled = habilitado, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = simbolo,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (habilitado) RentGoCores.Azul else RentGoCores.TextoCinza.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun RotuloDoCampo(texto: String) {
    Text(
        text = texto,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = RentGoCores.TextoCinza
    )
    Spacer(Modifier.height(6.dp))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DialogoCarro(
    carroParaEditar: Carro?,
    onFechar: () -> Unit,
    onSalvar: (String, Categoria, Double, String, Boolean, Int) -> Unit
) {
    var nome by remember { mutableStateOf(carroParaEditar?.nome ?: "") }
    var preco by remember { mutableStateOf(carroParaEditar?.let { textoDoPreco(it.precoDia) } ?: "") }
    var categoria by remember { mutableStateOf(carroParaEditar?.categoria ?: Categoria.SEDA) }
    var cambio by remember { mutableStateOf(carroParaEditar?.cambio ?: opcoesDeCambio.first()) }
    var arCondicionado by remember { mutableStateOf(carroParaEditar?.arCondicionado ?: true) }
    var lugares by remember {
        mutableStateOf((carroParaEditar?.lugares ?: 5).coerceIn(MIN_LUGARES, MAX_LUGARES))
    }
    var erro by remember { mutableStateOf<String?>(null) }
    val editando = carroParaEditar != null

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text(if (editando) "Editar carro" else "Novo carro", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it; erro = null },
                    label = { Text("Nome do carro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = preco,
                    onValueChange = { preco = it; erro = null },
                    label = { Text("Preço por dia (R$)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(14.dp))
                RotuloDoCampo("Categoria")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Categoria.values().forEach { opcao ->
                        OpcaoChip(opcao.rotulo, opcao == categoria) { categoria = opcao }
                    }
                }

                Spacer(Modifier.height(14.dp))
                RotuloDoCampo("Câmbio")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    opcoesDeCambio.forEach { opcao ->
                        OpcaoChip(opcao, opcao == cambio) { cambio = opcao }
                    }
                }

                Spacer(Modifier.height(14.dp))
                RotuloDoCampo("Ar-condicionado")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OpcaoChip("Com ar", arCondicionado) { arCondicionado = true }
                    OpcaoChip("Sem ar", !arCondicionado) { arCondicionado = false }
                }

                Spacer(Modifier.height(14.dp))
                RotuloDoCampo("Quantidade de lugares")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BotaoQuantidade("−", lugares > MIN_LUGARES) { lugares -= 1 }
                    Text(
                        text = "$lugares",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.TextoEscuro,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(48.dp)
                    )
                    BotaoQuantidade("+", lugares < MAX_LUGARES) { lugares += 1 }
                }

                erro?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = RentGoCores.Vermelho, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val valor = preco.replace(',', '.').toDoubleOrNull()
                when {
                    nome.isBlank() -> erro = "Informe o nome do carro."
                    valor == null || valor <= 0.0 -> erro = "Informe um preço válido."
                    else -> onSalvar(nome, categoria, valor, cambio, arCondicionado, lugares)
                }
            }) { Text(if (editando) "Salvar" else "Adicionar") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancelar") }
        }
    )
}

@Composable
fun TopoPesquisa(onVoltar: () -> Unit, onLimpar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(RentGoCores.Branco)
                .clickable(onClick = onVoltar),
            contentAlignment = Alignment.Center
        ) {
            Text("‹", fontSize = 22.sp, color = RentGoCores.TextoEscuro)
        }

        Text(
            text = "Pesquisa",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = RentGoCores.TextoEscuro,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(RentGoCores.Branco)
                .clickable(onClick = onLimpar),
            contentAlignment = Alignment.Center
        ) {
            Text("↻", fontSize = 20.sp, color = RentGoCores.Azul)
        }
    }
}

@Composable
fun LinhaLocalidadeEData(localidade: String, aoMudarLocalidade: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = localidade,
            onValueChange = aoMudarLocalidade,
            placeholder = { Text("Buscar carro pelo nome", color = RentGoCores.TextoCinza) },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .width(150.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(RentGoCores.Branco)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("15 Out - 20 Out", color = RentGoCores.TextoEscuro, fontSize = 14.sp)
        }
    }
}

@Composable
fun LinhaDeFiltros(
    filtros: List<String>,
    selecionados: List<Boolean>,
    aoClicarFiltro: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        filtros.forEachIndexed { indice, nome ->
            val estaSelecionado = selecionados[indice]

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (estaSelecionado) RentGoCores.Azul else RentGoCores.Branco)
                    .then(
                        if (!estaSelecionado)
                            Modifier.border(1.dp, RentGoCores.Borda, RoundedCornerShape(18.dp))
                        else Modifier
                    )
                    .clickable { aoClicarFiltro(indice) }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = nome,
                    fontSize = 13.sp,
                    color = if (estaSelecionado) RentGoCores.Branco else RentGoCores.TextoEscuro
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

@Composable
fun CardDoCarro(
    carro: Carro,
    aoAbrir: () -> Unit,
    aoRemover: () -> Unit,
    iconeAcao: ImageVector = Icons.Outlined.Delete,
    descricaoAcao: String = "Remover carro",
    aoEditar: (() -> Unit)? = null
) {
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
                    text = carro.nome,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.TextoEscuro
                )
                Text(
                    text = carro.categoria.rotulo,
                    fontSize = 12.sp,
                    color = RentGoCores.TextoCinza
                )

                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("★", fontSize = 13.sp, color = RentGoCores.Azul)
                    Text(" ${carro.nota}", fontSize = 13.sp, color = RentGoCores.TextoEscuro)
                }

                Text(
                    text = "${formatarPreco(carro.precoDia)} / dia",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.TextoEscuro
                )
            }

            if (aoEditar != null) {
                IconButton(onClick = aoEditar) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editar carro",
                        tint = RentGoCores.Azul
                    )
                }
            }

            IconButton(onClick = aoRemover) {
                Icon(
                    imageVector = iconeAcao,
                    contentDescription = descricaoAcao,
                    tint = RentGoCores.Vermelho
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TelaPesquisaPreview() {
    TelaPesquisa(onVoltar = {}, onAbrirCarro = {})
}
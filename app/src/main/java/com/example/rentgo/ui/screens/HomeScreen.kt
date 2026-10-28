package com.example.rentgo.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CarrosRepositorio
import com.example.rentgo.data.SessaoUsuario
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Categoria
import com.example.rentgo.model.Usuario
import com.example.rentgo.model.formatarPreco
import com.example.rentgo.ui.components.AvatarIniciais
import com.example.rentgo.ui.components.FotoCarro
import com.example.rentgo.ui.components.iconeDaCategoria
import com.example.rentgo.ui.components.coresCampoRentGo
import com.example.rentgo.ui.theme.RentGoCores

private enum class Ordenacao(val rotulo: String) {
    PADRAO("Ordem padrão"),
    MENOR_PRECO("Menor preço"),
    MAIOR_PRECO("Maior preço"),
    MELHOR_NOTA("Melhor avaliação")
}

@Composable
fun HomeScreen(
    onAbrirCarro: (Int) -> Unit,
    onVerTodas: () -> Unit,
    onAbrirPerfil: () -> Unit
) {
    val usuario = SessaoUsuario.usuarioLogado
    val context = LocalContext.current

    var ofertasDoBanco by remember { mutableStateOf(listOf<Carro>()) }
    var carregando by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        ofertasDoBanco = CarrosRepositorio.buscarOfertas(context)
        carregando = false
    }

    var busca by rememberSaveable { mutableStateOf("") }
    var categoriaSelecionada by rememberSaveable { mutableStateOf<Categoria?>(null) }
    var ordenacao by rememberSaveable { mutableStateOf(Ordenacao.PADRAO) }

    val termo = busca.trim()
    val ofertasFiltradas = ofertasDoBanco
        .filter { categoriaSelecionada == null || it.categoria == categoriaSelecionada }
        .filter {
            termo.isEmpty() ||
                    it.nome.contains(termo, ignoreCase = true) ||
                    it.categoria.rotulo.contains(termo, ignoreCase = true)
        }
    val ofertas = when (ordenacao) {
        Ordenacao.PADRAO -> ofertasFiltradas
        Ordenacao.MENOR_PRECO -> ofertasFiltradas.sortedBy { it.precoDia }
        Ordenacao.MAIOR_PRECO -> ofertasFiltradas.sortedByDescending { it.precoDia }
        Ordenacao.MELHOR_NOTA -> ofertasFiltradas.sortedByDescending { it.nota }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RentGoCores.Fundo)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))

        CabecalhoUsuario(
            usuario = usuario,
            quantidadeOfertas = ofertasDoBanco.size,
            onAbrirPerfil = onAbrirPerfil,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(22.dp))

        CampoBusca(
            valor = busca,
            onValorChange = { busca = it },
            ordenacao = ordenacao,
            onOrdenar = { ordenacao = it },
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(28.dp))

        TituloSecao(titulo = "Ofertas da Semana", acao = "Ver todas", onAcao = onVerTodas)

        Spacer(Modifier.height(14.dp))

        if (!carregando && ofertas.isEmpty()) {
            OfertasVazias(modifier = Modifier.padding(horizontal = 20.dp))
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(ofertas, key = { it.id }) { carro ->
                    CardOferta(carro = carro, onAbrir = { onAbrirCarro(carro.id) })
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        TituloSecao(titulo = "Categorias")

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Categoria.values().forEach { categoria ->
                CartaoCategoria(
                    categoria = categoria,
                    selecionada = categoria == categoriaSelecionada,
                    onClick = {
                        categoriaSelecionada = if (categoriaSelecionada == categoria) null else categoria
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        categoriaSelecionada?.let {
            Text(
                text = "Mostrando só ${it.rotulo} nas ofertas. Toque de novo para ver todas.",
                fontSize = 12.sp,
                color = RentGoCores.TextoCinza,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CabecalhoUsuario(
    usuario: Usuario?,
    quantidadeOfertas: Int,
    onAbrirPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primeiroNome = usuario?.primeiroNome ?: "visitante"
    var menuNotificacoes by remember { mutableStateOf(false) }
    var notificacoesLidas by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarIniciais(
            iniciais = usuario?.iniciais ?: "?",
            tamanho = 48.dp,
            onClick = onAbrirPerfil
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Olá, $primeiroNome 👋",
                fontSize = 13.sp,
                color = RentGoCores.TextoCinza,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Alugue hoje",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = RentGoCores.TextoEscuro
            )
        }

        Box {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(RentGoCores.Branco)
                    .border(1.dp, RentGoCores.Borda, RoundedCornerShape(14.dp))
                    .clickable {
                        menuNotificacoes = true
                        notificacoesLidas = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificações",
                    tint = RentGoCores.TextoEscuro,
                    modifier = Modifier.size(22.dp)
                )
                if (!notificacoesLidas) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 11.dp, end = 12.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(RentGoCores.Vermelho)
                    )
                }
            }

            DropdownMenu(
                expanded = menuNotificacoes,
                onDismissRequest = { menuNotificacoes = false }
            ) {
                Text(
                    text = "Notificações",
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.TextoEscuro,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                ItemNotificacao(
                    icone = Icons.Outlined.Person,
                    titulo = "Bem-vindo(a), $primeiroNome!",
                    texto = "Sua conta RentGo está pronta para alugar.",
                    onClick = { menuNotificacoes = false }
                )
                ItemNotificacao(
                    icone = Icons.Outlined.LocalOffer,
                    titulo = "Ofertas da semana",
                    texto = "$quantidadeOfertas carros com preço especial.",
                    onClick = { menuNotificacoes = false }
                )
            }
        }
    }
}

@Composable
private fun ItemNotificacao(
    icone: ImageVector,
    titulo: String,
    texto: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        leadingIcon = { Icon(icone, contentDescription = null, tint = RentGoCores.Azul) },
        text = {
            Column {
                Text(titulo, fontWeight = FontWeight.SemiBold, color = RentGoCores.TextoEscuro)
                Text(texto, fontSize = 12.sp, color = RentGoCores.TextoCinza)
            }
        },
        onClick = onClick
    )
}

@Composable
private fun CampoBusca(
    valor: String,
    onValorChange: (String) -> Unit,
    ordenacao: Ordenacao,
    onOrdenar: (Ordenacao) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuOrdenar by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Encontre o carro perfeito!") },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = {
            Box {
                IconButton(onClick = { menuOrdenar = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "Ordenar ofertas",
                        tint = RentGoCores.Azul
                    )
                }
                DropdownMenu(
                    expanded = menuOrdenar,
                    onDismissRequest = { menuOrdenar = false }
                ) {
                    Ordenacao.values().forEach { opcao ->
                        DropdownMenuItem(
                            text = { Text(opcao.rotulo) },
                            onClick = {
                                onOrdenar(opcao)
                                menuOrdenar = false
                            },
                            leadingIcon = {
                                if (opcao == ordenacao) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = RentGoCores.Azul)
                                } else {
                                    Spacer(Modifier.size(24.dp))
                                }
                            }
                        )
                    }
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = coresCampoRentGo()
    )
}

@Composable
private fun TituloSecao(
    titulo: String,
    acao: String? = null,
    onAcao: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = RentGoCores.TextoEscuro,
            modifier = Modifier.weight(1f)
        )
        if (acao != null) {
            Text(
                text = acao,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = RentGoCores.Azul,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onAcao)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun CardOferta(carro: Carro, onAbrir: () -> Unit) {
    Card(
        onClick = onAbrir,
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = RentGoCores.Branco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        FotoCarro(
            carro = carro,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = carro.nome,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.TextoEscuro,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = carro.categoria.rotulo,
                        fontSize = 13.sp,
                        color = RentGoCores.TextoCinza
                    )
                }
                Spacer(Modifier.width(8.dp))
                SeloNota(nota = carro.nota)
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = RentGoCores.Borda)
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = RentGoCores.TextoEscuro
                            )
                        ) { append(formatarPreco(carro.precoDia)) }
                        withStyle(SpanStyle(fontSize = 13.sp, color = RentGoCores.TextoCinza)) {
                            append("/ dia")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(RentGoCores.Azul)
                        .clickable(onClick = onAbrir),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "Ver detalhes de ${carro.nome}",
                        tint = RentGoCores.Branco
                    )
                }
            }
        }
    }
}

@Composable
private fun SeloNota(nota: Double) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(RentGoCores.AzulClaro)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.StarBorder,
            contentDescription = null,
            tint = RentGoCores.Azul,
            modifier = Modifier.size(15.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = nota.toString(),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = RentGoCores.Azul
        )
    }
}

@Composable
private fun OfertasVazias(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(RentGoCores.Branco)
            .border(1.dp, RentGoCores.Borda, RoundedCornerShape(20.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.DirectionsCar,
            contentDescription = null,
            tint = RentGoCores.TextoCinza,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Nenhum carro encontrado",
            fontWeight = FontWeight.SemiBold,
            color = RentGoCores.TextoEscuro
        )
        Text(
            text = "Tente outra busca ou outra categoria.",
            fontSize = 13.sp,
            color = RentGoCores.TextoCinza,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CartaoCategoria(
    categoria: Categoria,
    selecionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(20.dp)
    val fundo by animateColorAsState(
        targetValue = if (selecionada) RentGoCores.Azul else RentGoCores.Branco,
        label = "fundoCategoria"
    )

    Column(
        modifier = modifier
            .height(100.dp)
            .clip(forma)
            .background(fundo)
            .border(1.dp, if (selecionada) RentGoCores.Azul else RentGoCores.Borda, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (selecionada) RentGoCores.Branco.copy(alpha = 0.18f) else RentGoCores.AzulClaro
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconeDaCategoria(categoria),
                contentDescription = null,
                tint = if (selecionada) RentGoCores.Branco else RentGoCores.Azul,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = categoria.rotulo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = if (selecionada) RentGoCores.Branco else RentGoCores.TextoEscuro
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(onAbrirCarro = {}, onVerTodas = {}, onAbrirPerfil = {})
}
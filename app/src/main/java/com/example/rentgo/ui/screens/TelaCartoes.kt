package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CartoesRepositorio
import com.example.rentgo.model.CartaoSalvo
import com.example.rentgo.model.descobrirBandeira
import com.example.rentgo.model.isCartaoVencido
import com.example.rentgo.ui.components.PaymentTopBar
import com.example.rentgo.ui.theme.RentGoCores

/**
 * "Meus cartões" (aberta pelo botão "Gerenciar" da tela de Pagamento).
 * Lista os cartões salvos no Room (LazyColumn + Card).
 *  - BUSCAR: LaunchedEffect(Unit) chama dao.buscarPorUsuario(...) ao abrir a tela;
 *  - ADICIONAR: botão "Adicionar cartão" (formulário em AlertDialog) -> dao.inserir(...);
 *  - REMOVER: lixeira em cada card -> dao.deletar(...);
 *  - clicar no card abre o Detalhe do cartão (editar).
 */
@Composable
fun TelaCartoes(
    onVoltar: () -> Unit,
    onAbrirCartao: (Int) -> Unit
) {
    val context = LocalContext.current
    val usuarioId = CartoesRepositorio.usuarioAtualId()

    var cartoes by remember { mutableStateOf<List<CartaoSalvo>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        cartoes = CartoesRepositorio.buscarDoUsuario(context, usuarioId)
        carregando = false
    }

    if (mostrarDialogo) {
        DialogoNovoCartao(
            usuarioId = usuarioId,
            onFechar = { mostrarDialogo = false },
            onSalvar = { novo ->
                CartoesRepositorio.inserir(context, novo) { cartoes = it }
                mostrarDialogo = false
            }
        )
    }

    Scaffold(
        topBar = { PaymentTopBar(titulo = "Meus cartões", onVoltar = onVoltar) },
        containerColor = RentGoCores.Fundo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RentGoCores.Fundo)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { mostrarDialogo = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RentGoCores.Azul)
            ) {
                Text("ADICIONAR CARTÃO", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!carregando && cartoes.isEmpty()) {
                Text(
                    text = "Você ainda não salvou nenhum cartão.\nSalve um para pagar suas reservas mais rápido.",
                    fontSize = 14.sp,
                    color = RentGoCores.TextoCinza,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartoes, key = { it.id }) { cartao ->
                    CardDoCartaoSalvo(
                        cartao = cartao,
                        aoAbrir = { onAbrirCartao(cartao.id) },
                        aoRemover = {
                            CartoesRepositorio.deletar(context, cartao) { cartoes = it }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun CardDoCartaoSalvo(cartao: CartaoSalvo, aoAbrir: () -> Unit, aoRemover: () -> Unit) {
    val vencido = isCartaoVencido(cartao.validade)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = aoAbrir),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartao.descricao,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "${cartao.apelido} · ${cartao.nomeTitular}",
                    fontSize = 12.sp,
                    color = RentGoCores.TextoCinza
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "Validade ${cartao.validade}",
                        fontSize = 12.sp,
                        color = if (vencido) RentGoCores.Vermelho else RentGoCores.TextoCinza
                    )
                    if (vencido) {
                        Text(" · VENCIDO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RentGoCores.Vermelho)
                    }
                    if (cartao.padrao) {
                        Text(" · PADRÃO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RentGoCores.Azul)
                    }
                }
            }
            IconButton(onClick = aoRemover) {
                Icon(Icons.Outlined.Delete, contentDescription = "Remover cartão", tint = RentGoCores.Vermelho)
            }
        }
    }
}

/** Formulário "Novo cartão". Valida com as mesmas funções da tela de Pagamento. */
@Composable
private fun DialogoNovoCartao(
    usuarioId: Int,
    onFechar: () -> Unit,
    onSalvar: (CartaoSalvo) -> Unit
) {
    var numero by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }
    var titular by remember { mutableStateOf("") }
    var apelido by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Novo cartão", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = numero,
                    onValueChange = { numero = it.filter { c -> c.isDigit() }.take(16); erro = null },
                    label = { Text("Número do cartão") },
                    placeholder = { Text("0000000000000000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = coresDosCampos(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = validade,
                    onValueChange = { validade = formatarValidade(it); erro = null },
                    label = { Text("Validade (MM/AA)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = coresDosCampos(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = titular,
                    onValueChange = { titular = it.uppercase(); erro = null },
                    label = { Text("Nome do titular") },
                    singleLine = true,
                    colors = coresDosCampos(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = apelido,
                    onValueChange = { apelido = it; erro = null },
                    label = { Text("Apelido (opcional)") },
                    singleLine = true,
                    colors = coresDosCampos(),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Guardamos só os 4 últimos dígitos. O CVV nunca é salvo.",
                    fontSize = 11.sp,
                    color = RentGoCores.TextoCinza,
                    modifier = Modifier.padding(top = 8.dp)
                )
                erro?.let {
                    Text(it, color = RentGoCores.Vermelho, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    !isNumeroCartaoValido(numero) -> erro = "O número do cartão precisa ter 16 dígitos."
                    !isValidadeValida(validade) -> erro = "Informe a validade no formato MM/AA."
                    isCartaoVencido(validade) -> erro = "Esse cartão está vencido."
                    !isNomeTitularValido(titular) -> erro = "Informe o nome do titular."
                    else -> {
                        val bandeira = descobrirBandeira(numero)
                        val ultimos = numero.takeLast(4)
                        onSalvar(
                            CartaoSalvo(
                                usuarioId = usuarioId,
                                apelido = apelido.trim().ifBlank { "$bandeira final $ultimos" },
                                nomeTitular = titular.trim(),
                                ultimosDigitos = ultimos,
                                bandeira = bandeira,
                                validade = validade
                            )
                        )
                    }
                }
            }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onFechar) { Text("Cancelar") }
        }
    )
}

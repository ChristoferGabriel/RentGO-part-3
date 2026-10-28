package com.example.rentgo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.CartoesRepositorio
import com.example.rentgo.model.CartaoSalvo
import com.example.rentgo.model.isCartaoVencido
import com.example.rentgo.ui.components.PaymentTopBar
import com.example.rentgo.ui.theme.RentGoCores

/**
 * Detalhe do cartão. Recebe só o id pela rota e BUSCA O CARTÃO NO BANCO
 * (LaunchedEffect + CartoesRepositorio.buscarPorId -> dao.buscarPorId(id)).
 *
 * Aqui dá para EDITAR (apelido, titular, validade e "cartão padrão" -> dao.atualizar)
 * e REMOVER (dao.deletar). O número do cartão não é editável: para trocar o cartão, salve outro.
 */
@Composable
fun TelaDetalheCartao(cartaoId: Int, onVoltar: () -> Unit) {
    val context = LocalContext.current

    var cartao by remember { mutableStateOf<CartaoSalvo?>(null) }
    var carregando by remember { mutableStateOf(true) }

    var apelido by remember { mutableStateOf("") }
    var titular by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }
    var padrao by remember { mutableStateOf(false) }
    var erro by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(cartaoId) {
        val encontrado = CartoesRepositorio.buscarPorId(context, cartaoId)
        cartao = encontrado
        if (encontrado != null) {
            apelido = encontrado.apelido
            titular = encontrado.nomeTitular
            validade = encontrado.validade
            padrao = encontrado.padrao
        }
        carregando = false
    }

    Scaffold(
        topBar = { PaymentTopBar(titulo = "Detalhe do cartão", onVoltar = onVoltar) },
        containerColor = RentGoCores.Fundo
    ) { paddingValues ->
        val atual = cartao

        if (atual == null) {
            Text(
                text = if (carregando) "Carregando..." else "Cartão não encontrado (talvez já tenha sido removido).",
                color = RentGoCores.TextoCinza,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RentGoCores.Fundo)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RentGoCores.Azul),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(atual.bandeira.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.85f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("•••• •••• •••• ${atual.ultimosDigitos}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(atual.nomeTitular, fontSize = 13.sp, color = Color.White)
                    Text(
                        text = "Validade ${atual.validade}" + if (isCartaoVencido(atual.validade)) " · VENCIDO" else "",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Editar cartão", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))

            LabelDoCampo("APELIDO")
            OutlinedTextField(
                value = apelido,
                onValueChange = { apelido = it; erro = null },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coresDosCampos(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LabelDoCampo("NOME DO TITULAR")
            OutlinedTextField(
                value = titular,
                onValueChange = { titular = it.uppercase(); erro = null },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coresDosCampos(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LabelDoCampo("VALIDADE")
            OutlinedTextField(
                value = validade,
                onValueChange = { validade = formatarValidade(it); erro = null },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("MM/AA") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = coresDosCampos(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Cartão padrão", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Já vem selecionado na hora de pagar", fontSize = 12.sp, color = RentGoCores.TextoCinza)
                }
                Switch(
                    checked = padrao,
                    onCheckedChange = { padrao = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = RentGoCores.Azul)
                )
            }

            erro?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = RentGoCores.Vermelho, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        !isNomeTitularValido(titular) -> erro = "Informe o nome do titular."
                        !isValidadeValida(validade) -> erro = "Informe a validade no formato MM/AA."
                        else -> {
                            val atualizado = atual.copy(
                                apelido = apelido.trim().ifBlank { atual.descricao },
                                nomeTitular = titular.trim(),
                                validade = validade,
                                // não deixa ficar sem nenhum padrão: quem já era padrão continua sendo
                                padrao = padrao || atual.padrao
                            )
                            CartoesRepositorio.atualizar(context, atualizado) { onVoltar() }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RentGoCores.Azul)
            ) {
                Text("SALVAR ALTERAÇÕES", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { CartoesRepositorio.deletar(context, atual) { onVoltar() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, RentGoCores.Vermelho),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RentGoCores.Vermelho)
            ) {
                Text("REMOVER CARTÃO", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

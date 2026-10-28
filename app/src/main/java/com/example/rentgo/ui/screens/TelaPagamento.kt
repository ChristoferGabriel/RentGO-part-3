package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.rentgo.data.CartoesRepositorio
import com.example.rentgo.model.CartaoSalvo
import com.example.rentgo.model.Carro
import com.example.rentgo.model.DadosReserva
import com.example.rentgo.model.descobrirBandeira
import com.example.rentgo.model.isCartaoVencido
import com.example.rentgo.model.formatarMoeda
import com.example.rentgo.ui.components.PaymentTopBar
import com.example.rentgo.ui.theme.RentGoCores
import kotlin.math.roundToLong

private val CinzaBotaoDesabilitado = Color(0xFFB7BDC9)

private const val DESCONTO_PIX_PERCENTUAL = 5L
private const val DIARIAS_PADRAO = 5
private const val PERIODO_PADRAO = "15 Out - 20 Out (5 Diárias)"

enum class FormaPagamento {
    CARTAO_CREDITO,
    PIX
}

class PagamentoViewModel : ViewModel() {

    var reserva by mutableStateOf(
        DadosReserva(
            nomeCarro = "Ford Focus 2023",
            periodo = PERIODO_PADRAO,
            valorCentavos = 60_000L
        )
    )
        private set

    var formaPagamento by mutableStateOf(FormaPagamento.CARTAO_CREDITO)
        private set
    var numeroCartao by mutableStateOf("")
        private set
    var validade by mutableStateOf("")
        private set
    var cvv by mutableStateOf("")
        private set
    var nomeTitular by mutableStateOf("")
        private set
    var pagamentoConcluido by mutableStateOf(false)
        private set

    // ---- cartões salvos (Room) ----
    var cartoesSalvos by mutableStateOf<List<CartaoSalvo>>(emptyList())
        private set
    /** Cartão salvo escolhido para pagar. null = digitar um cartão novo. */
    var cartaoSelecionado by mutableStateOf<CartaoSalvo?>(null)
        private set
    /** true depois que o usuário toca em "Usar outro cartão" (evita escolher o padrão de novo sozinho). */
    private var usandoNovoCartao by mutableStateOf(false)
    var salvarCartao by mutableStateOf(false)
        private set
    /** Texto mostrado na tela de Reserva concluída, ex.: "Visa •••• 4242" ou "Pix". */
    var descricaoPagamento by mutableStateOf("")
        private set

    var carroReservado by mutableStateOf<Carro?>(null)
        private set
    var dias by mutableStateOf(DIARIAS_PADRAO)
        private set

    val descontoCentavos: Long
        get() = if (formaPagamento == FormaPagamento.PIX) {
            reserva.valorCentavos * DESCONTO_PIX_PERCENTUAL / 100
        } else {
            0L
        }

    val valorFinalCentavos: Long
        get() = reserva.valorCentavos - descontoCentavos

    val cartaoSelecionadoVencido: Boolean
        get() = cartaoSelecionado?.let { isCartaoVencido(it.validade) } ?: false

    /**
     * Pix: sempre pode. Cartão salvo: só precisa do CVV (e não pode estar vencido).
     * Cartão novo: formulário completo válido.
     */
    val podeConfirmar: Boolean
        get() = formaPagamento == FormaPagamento.PIX ||
                (cartaoSelecionado?.let { !isCartaoVencido(it.validade) && isCvvValido(cvv) }
                    ?: isFormularioValido(numeroCartao, validade, cvv, nomeTitular))

    fun iniciarReserva(carro: Carro, diarias: Int = DIARIAS_PADRAO) {
        carroReservado = carro
        dias = diarias
        reserva = DadosReserva(
            nomeCarro = carro.nome,
            periodo = if (diarias == DIARIAS_PADRAO) PERIODO_PADRAO
            else "$diarias ${if (diarias == 1) "Diária" else "Diárias"}",
            valorCentavos = (carro.precoDia * diarias * 100).roundToLong()
        )
        novaReserva()
    }

    fun selecionarFormaPagamento(forma: FormaPagamento) { formaPagamento = forma }
    fun alterarNumeroCartao(valor: String) { numeroCartao = valor }
    fun alterarValidade(valor: String) { validade = valor }
    fun alterarCvv(valor: String) { cvv = valor }
    fun alterarNomeTitular(valor: String) { nomeTitular = valor }

    /** Chamado quando as telas leem os cartões do banco: guarda a lista e escolhe o cartão padrão. */
    fun carregarCartoes(lista: List<CartaoSalvo>) {
        cartoesSalvos = lista
        // atualiza (ou descarta, se foi removido) o cartão que estava selecionado
        cartaoSelecionado = cartaoSelecionado?.let { atual -> lista.find { it.id == atual.id } }
        if (cartaoSelecionado == null && !usandoNovoCartao) {
            cartaoSelecionado = lista.firstOrNull { it.padrao } ?: lista.firstOrNull()
        }
    }

    fun selecionarCartaoSalvo(cartao: CartaoSalvo) {
        cartaoSelecionado = cartao
        usandoNovoCartao = false
        cvv = ""
    }

    fun usarNovoCartao() {
        cartaoSelecionado = null
        usandoNovoCartao = true
        cvv = ""
    }

    fun alterarSalvarCartao(valor: Boolean) { salvarCartao = valor }

    fun confirmarPagamento(): Boolean {
        if (!podeConfirmar) return false
        descricaoPagamento = when {
            formaPagamento == FormaPagamento.PIX -> "Pix"
            cartaoSelecionado != null -> cartaoSelecionado!!.descricao
            else -> "Cartão de crédito •••• ${numeroCartao.filter { it.isDigit() }.takeLast(4)}"
        }
        pagamentoConcluido = true
        return true
    }

    fun novaReserva() {
        formaPagamento = FormaPagamento.CARTAO_CREDITO
        numeroCartao = ""
        validade = ""
        cvv = ""
        nomeTitular = ""
        pagamentoConcluido = false
        cartaoSelecionado = null
        usandoNovoCartao = false
        salvarCartao = false
        descricaoPagamento = ""
    }
}

@Composable
fun TelaPagamento(
    viewModel: PagamentoViewModel,
    onVoltar: () -> Unit,
    onPagamentoConfirmado: () -> Unit,
    onGerenciarCartoes: () -> Unit = {}
) {
    val context = LocalContext.current
    val reserva = viewModel.reserva
    val pagaComPix = viewModel.formaPagamento == FormaPagamento.PIX

    // Ao abrir a tela (e ao voltar de "Meus cartões"), busca os cartões salvos no banco.
    LaunchedEffect(Unit) {
        viewModel.carregarCartoes(
            CartoesRepositorio.buscarDoUsuario(context, CartoesRepositorio.usuarioAtualId())
        )
    }

    Scaffold(
        topBar = { PaymentTopBar(onVoltar = onVoltar) },
        containerColor = RentGoCores.Fundo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RentGoCores.Fundo)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            ResumoDaReserva(
                nomeCarro = reserva.nomeCarro,
                periodo = reserva.periodo,
                subtotal = formatarMoeda(reserva.valorCentavos),
                desconto = if (pagaComPix) formatarMoeda(viewModel.descontoCentavos) else null,
                valorTotal = formatarMoeda(viewModel.valorFinalCentavos)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Forma de Pagamento",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            FormasDePagamento(
                selecionada = viewModel.formaPagamento,
                onSelecionar = viewModel::selecionarFormaPagamento
            )

            if (!pagaComPix) {
                Spacer(modifier = Modifier.height(20.dp))

                CartoesSalvosSecao(
                    cartoes = viewModel.cartoesSalvos,
                    selecionado = viewModel.cartaoSelecionado,
                    onSelecionar = viewModel::selecionarCartaoSalvo,
                    onUsarOutro = viewModel::usarNovoCartao,
                    onGerenciar = onGerenciarCartoes
                )

                Spacer(modifier = Modifier.height(20.dp))

                val cartaoSalvo = viewModel.cartaoSelecionado
                if (cartaoSalvo != null) {
                    CampoCvvDoCartaoSalvo(
                        cartao = cartaoSalvo,
                        cvv = viewModel.cvv,
                        onCvvChange = { viewModel.alterarCvv(it.filter { c -> c.isDigit() }.take(3)) },
                        vencido = viewModel.cartaoSelecionadoVencido
                    )
                } else {
                    CamposDoCartao(
                        numeroCartao = viewModel.numeroCartao,
                        onNumeroCartaoChange = viewModel::alterarNumeroCartao,
                        validade = viewModel.validade,
                        onValidadeChange = viewModel::alterarValidade,
                        cvv = viewModel.cvv,
                        onCvvChange = viewModel::alterarCvv,
                        nomeTitular = viewModel.nomeTitular,
                        onNomeTitularChange = viewModel::alterarNomeTitular
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OpcaoSalvarCartao(
                        marcado = viewModel.salvarCartao,
                        onMudar = viewModel::alterarSalvarCartao
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotaoConfirmarPagamento(
                habilitado = viewModel.podeConfirmar,
                onConfirmar = {
                    // cartão digitado + caixa "Salvar" marcada -> guarda no Room (só 4 últimos dígitos)
                    val cartaoParaSalvar = if (!pagaComPix && viewModel.cartaoSelecionado == null && viewModel.salvarCartao) {
                        val digitos = viewModel.numeroCartao.filter { it.isDigit() }
                        val bandeira = descobrirBandeira(digitos)
                        CartaoSalvo(
                            usuarioId = CartoesRepositorio.usuarioAtualId(),
                            apelido = "$bandeira final ${digitos.takeLast(4)}",
                            nomeTitular = viewModel.nomeTitular.trim(),
                            ultimosDigitos = digitos.takeLast(4),
                            bandeira = bandeira,
                            validade = viewModel.validade
                        )
                    } else null

                    if (viewModel.confirmarPagamento()) {
                        cartaoParaSalvar?.let { CartoesRepositorio.inserir(context, it) }
                        onPagamentoConfirmado()
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
internal fun coresDosCampos() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedPlaceholderColor = Color.Black.copy(alpha = 0.4f),
    unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.4f),
    focusedBorderColor = RentGoCores.Azul,
    unfocusedBorderColor = Color(0xFFBDBDBD),
    cursorColor = RentGoCores.Azul
)

@Composable
fun ResumoDaReserva(
    nomeCarro: String,
    periodo: String,
    subtotal: String,
    desconto: String?,
    valorTotal: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RESUMO DA RESERVA",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RentGoCores.TextoCinza
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text(
                    text = nomeCarro,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = periodo,
                    fontSize = 13.sp,
                    color = RentGoCores.TextoCinza
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp, color = RentGoCores.Fundo)
            Spacer(modifier = Modifier.height(12.dp))

            if (desconto != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Subtotal", fontSize = 13.sp, color = RentGoCores.TextoCinza)
                    Text(text = subtotal, fontSize = 13.sp, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Desconto Pix ($DESCONTO_PIX_PERCENTUAL%)",
                        fontSize = 13.sp,
                        color = RentGoCores.TextoCinza
                    )
                    Text(
                        text = "- $desconto",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RentGoCores.Verde
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 1.dp, color = RentGoCores.Fundo)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Valor Total", fontSize = 13.sp, color = RentGoCores.TextoCinza)
                Text(
                    text = valorTotal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = RentGoCores.Verde
                )
            }
        }
    }
}

@Composable
fun FormasDePagamento(
    selecionada: FormaPagamento,
    onSelecionar: (FormaPagamento) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        OpcaoDePagamento(
            titulo = "Cartão de Crédito",
            subtitulo = null,
            selecionada = selecionada == FormaPagamento.CARTAO_CREDITO,
            onClick = { onSelecionar(FormaPagamento.CARTAO_CREDITO) },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        OpcaoDePagamento(
            titulo = "Pix",
            subtitulo = "Desconto de $DESCONTO_PIX_PERCENTUAL%",
            selecionada = selecionada == FormaPagamento.PIX,
            onClick = { onSelecionar(FormaPagamento.PIX) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun OpcaoDePagamento(
    titulo: String,
    subtitulo: String?,
    selecionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(
                width = if (selecionada) 2.dp else 1.dp,
                color = if (selecionada) RentGoCores.Azul else Color(0xFFBDBDBD),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = titulo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                if (subtitulo != null) {
                    Text(text = subtitulo, fontSize = 11.sp, color = RentGoCores.TextoCinza)
                }
            }
            RadioButton(
                selected = selecionada,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = RentGoCores.Azul,
                    unselectedColor = RentGoCores.TextoCinza
                )
            )
        }
    }
}

@Composable
fun CamposDoCartao(
    numeroCartao: String,
    onNumeroCartaoChange: (String) -> Unit,
    validade: String,
    onValidadeChange: (String) -> Unit,
    cvv: String,
    onCvvChange: (String) -> Unit,
    nomeTitular: String,
    onNomeTitularChange: (String) -> Unit
) {
    Column {
        LabelDoCampo("NÚMERO DO CARTÃO")
        OutlinedTextField(
            colors = coresDosCampos(),
            value = numeroCartao,
            onValueChange = { novoValor -> onNumeroCartaoChange(novoValor) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("0000 0000 0000 0000") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                LabelDoCampo("VALIDADE")
                OutlinedTextField(
                    colors = coresDosCampos(),
                    value = validade,
                    onValueChange = { novoValor -> onValidadeChange(formatarValidade(novoValor)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("MM/AA") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                LabelDoCampo("CVV")
                OutlinedTextField(
                    colors = coresDosCampos(),
                    value = cvv,
                    onValueChange = { novoValor -> onCvvChange(novoValor) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("•••") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LabelDoCampo("NOME DO TITULAR")
        OutlinedTextField(
            colors = coresDosCampos(),
            value = nomeTitular,
            onValueChange = { novoValor -> onNomeTitularChange(novoValor.uppercase()) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("NOME COMO ESTÁ NO CARTÃO") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
    }
}

/** Lista de cartões salvos (vindos do Room) + "Usar outro cartão" + atalho para gerenciar. */
@Composable
fun CartoesSalvosSecao(
    cartoes: List<CartaoSalvo>,
    selecionado: CartaoSalvo?,
    onSelecionar: (CartaoSalvo) -> Unit,
    onUsarOutro: () -> Unit,
    onGerenciar: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Meus cartões",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onGerenciar) {
                Text("Gerenciar", color = RentGoCores.Azul, fontWeight = FontWeight.Bold)
            }
        }

        if (cartoes.isEmpty()) {
            Text(
                text = "Nenhum cartão salvo ainda. Marque \"Salvar este cartão\" abaixo para pagar mais rápido na próxima vez.",
                fontSize = 12.sp,
                color = RentGoCores.TextoCinza
            )
        } else {
            cartoes.forEach { cartao ->
                val vencido = isCartaoVencido(cartao.validade)
                OpcaoDePagamento(
                    titulo = cartao.descricao + if (cartao.padrao) "  •  Padrão" else "",
                    subtitulo = "${cartao.apelido} · Validade ${cartao.validade}" +
                        if (vencido) " · VENCIDO" else "",
                    selecionada = selecionado?.id == cartao.id,
                    onClick = { onSelecionar(cartao) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            OpcaoDePagamento(
                titulo = "Usar outro cartão",
                subtitulo = null,
                selecionada = selecionado == null,
                onClick = onUsarOutro,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Com cartão salvo, só falta o CVV (ele nunca é guardado no banco). */
@Composable
fun CampoCvvDoCartaoSalvo(
    cartao: CartaoSalvo,
    cvv: String,
    onCvvChange: (String) -> Unit,
    vencido: Boolean
) {
    Column {
        LabelDoCampo("CVV DO CARTÃO ${cartao.bandeira.uppercase()} •••• ${cartao.ultimosDigitos}")
        OutlinedTextField(
            colors = coresDosCampos(),
            value = cvv,
            onValueChange = onCvvChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("•••") },
            singleLine = true,
            enabled = !vencido,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(10.dp)
        )
        if (vencido) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Este cartão está vencido. Escolha outro ou use outro cartão.",
                fontSize = 12.sp,
                color = RentGoCores.Vermelho
            )
        }
    }
}

@Composable
fun OpcaoSalvarCartao(marcado: Boolean, onMudar: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMudar(!marcado) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = marcado,
            onCheckedChange = onMudar,
            colors = CheckboxDefaults.colors(checkedColor = RentGoCores.Azul)
        )
        Column {
            Text(text = "Salvar este cartão", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(
                text = "Guardamos só os 4 últimos dígitos. O CVV nunca é salvo.",
                fontSize = 11.sp,
                color = RentGoCores.TextoCinza
            )
        }
    }
}

@Composable
fun LabelDoCampo(texto: String) {
    Text(
        text = texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
fun BotaoConfirmarPagamento(habilitado: Boolean, onConfirmar: () -> Unit) {
    Button(
        onClick = { onConfirmar() },
        enabled = habilitado,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RentGoCores.Azul,
            disabledContainerColor = CinzaBotaoDesabilitado
        )
    ) {
        Text(
            text = "CONFIRMAR PAGAMENTO",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

fun formatarValidade(entrada: String): String {
    val apenasDigitos = entrada.filter { caractere -> caractere.isDigit() }.take(4)

    return if (apenasDigitos.length > 2) {
        val mes = apenasDigitos.substring(0, 2)
        val ano = apenasDigitos.substring(2)
        "$mes/$ano"
    } else {
        apenasDigitos
    }
}

fun isNumeroCartaoValido(numero: String): Boolean {
    val apenasNumeros = numero.replace(" ", "")
    if (apenasNumeros.length != 16) return false
    return apenasNumeros.all { caractere -> caractere.isDigit() }
}

fun isValidadeValida(validade: String): Boolean {
    val partes = validade.split("/")
    if (partes.size != 2) return false

    val mes = partes[0]
    val ano = partes[1]

    if (mes.length != 2 || ano.length != 2) return false
    if (!mes.all { it.isDigit() } || !ano.all { it.isDigit() }) return false

    val mesNumero = mes.toInt()
    return mesNumero in 1..12
}

fun isCvvValido(cvv: String): Boolean {
    if (cvv.length != 3) return false
    return cvv.all { caractere -> caractere.isDigit() }
}

fun isNomeTitularValido(nome: String): Boolean {
    return nome.trim().isNotEmpty()
}

fun isFormularioValido(
    numeroCartao: String,
    validade: String,
    cvv: String,
    nomeTitular: String
): Boolean {
    return isNumeroCartaoValido(numeroCartao) &&
            isValidadeValida(validade) &&
            isCvvValido(cvv) &&
            isNomeTitularValido(nomeTitular)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TelaPagamentoPreview() {
    TelaPagamento(
        viewModel = PagamentoViewModel(),
        onVoltar = {},
        onPagamentoConfirmado = {}
    )
}

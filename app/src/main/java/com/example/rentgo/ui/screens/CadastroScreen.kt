package com.example.rentgo.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.SessaoUsuario
import com.example.rentgo.ui.components.CampoRentGo
import com.example.rentgo.ui.theme.RentGoCores

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroScreen(
    onVoltar: () -> Unit,
    onCadastrado: () -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var confirmarSenha by rememberSaveable { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var salvando by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun cadastrar() {
        erro = when {
            nome.isBlank() -> "Informe seu nome."
            !email.contains("@") || !email.contains(".") -> "Informe um e-mail válido."
            senha.length < 6 -> "A senha precisa ter pelo menos 6 caracteres."
            senha != confirmarSenha -> "As senhas não são iguais."
            else -> null
        }
        if (erro != null || salvando) return

        salvando = true
        SessaoUsuario.cadastrar(context, nome, email, senha) { mensagemDeErro ->
            salvando = false
            erro = mensagemDeErro
            if (mensagemDeErro == null) onCadastrado()
        }
    }

    Scaffold(
        containerColor = RentGoCores.Fundo,
        topBar = {
            TopAppBar(
                title = { Text("Criar conta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RentGoCores.Fundo,
                    titleContentColor = RentGoCores.TextoEscuro,
                    navigationIconContentColor = RentGoCores.TextoEscuro
                )
            )
        }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Seu primeiro nome vai aparecer na tela inicial do app.",
                color = RentGoCores.TextoCinza,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(24.dp))

            CampoRentGo(
                rotulo = "NOME",
                valor = nome,
                onValorChange = { nome = it; erro = null },
                placeholder = "Seu nome completo",
                icone = Icons.Outlined.PersonOutline
            )
            Spacer(Modifier.height(18.dp))
            CampoRentGo(
                rotulo = "E-MAIL",
                valor = email,
                onValorChange = { email = it; erro = null },
                placeholder = "exemplo@rentgo.com.br",
                icone = Icons.Outlined.Email,
                tipoTeclado = KeyboardType.Email
            )
            Spacer(Modifier.height(18.dp))
            CampoRentGo(
                rotulo = "SENHA",
                valor = senha,
                onValorChange = { senha = it; erro = null },
                placeholder = "Mínimo de 6 caracteres",
                icone = Icons.Outlined.Lock,
                ehSenha = true
            )
            Spacer(Modifier.height(18.dp))
            CampoRentGo(
                rotulo = "CONFIRMAR SENHA",
                valor = confirmarSenha,
                onValorChange = { confirmarSenha = it; erro = null },
                placeholder = "Repita a senha",
                icone = Icons.Outlined.Lock,
                ehSenha = true,
                acaoTeclado = ImeAction.Done,
                onConcluir = { cadastrar() }
            )

            erro?.let {
                Spacer(Modifier.height(12.dp))
                Text(text = it, color = RentGoCores.Vermelho, fontSize = 13.sp)
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = { cadastrar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RentGoCores.Azul,
                    contentColor = RentGoCores.Branco
                )
            ) {
                Text(if (salvando) "SALVANDO..." else "CRIAR CONTA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

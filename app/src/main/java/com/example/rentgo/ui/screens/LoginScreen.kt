package com.example.rentgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.SessaoUsuario
import com.example.rentgo.ui.components.CampoRentGo
import com.example.rentgo.ui.components.LogoRentGo
import com.example.rentgo.ui.components.coresCampoRentGo
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun LoginScreen(
    onEntrar: () -> Unit,
    onCriarConta: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var mostrarRecuperarSenha by remember { mutableStateOf(false) }
    var entrando by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun tentarEntrar() {
        if (email.isBlank() || senha.isBlank()) {
            erro = "Preencha e-mail e senha."
            return
        }
        if (entrando) return
        entrando = true
        SessaoUsuario.entrar(context, email, senha) { deuCerto ->
            entrando = false
            if (deuCerto) {
                onEntrar()
            } else {
                erro = "E-mail ou senha incorretos. Ainda não tem conta? Toque em Criar conta."
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(RentGoCores.Fundo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))
            LogoRentGo()
            Spacer(Modifier.height(40.dp))

            Text(
                text = "BEM VINDO A RENTGO - ALUGUE JÁ!",
                color = RentGoCores.Azul,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            CampoRentGo(
                rotulo = "E-MAIL",
                valor = email,
                onValorChange = { email = it; erro = null },
                placeholder = "exemplo@rentgo.com.br",
                icone = Icons.Outlined.PersonOutline,
                tipoTeclado = KeyboardType.Email
            )

            Spacer(Modifier.height(20.dp))

            CampoRentGo(
                rotulo = "SENHA",
                valor = senha,
                onValorChange = { senha = it; erro = null },
                placeholder = "••••••••••••",
                icone = Icons.Outlined.Lock,
                ehSenha = true,
                acaoTeclado = ImeAction.Done,
                onConcluir = { tentarEntrar() }
            )

            erro?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    color = RentGoCores.Vermelho,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { tentarEntrar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RentGoCores.Azul,
                    contentColor = RentGoCores.Branco
                )
            ) {
                Text(if (entrando) "ENTRANDO..." else "ENTRAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(32.dp))

            Text(
                text = "Recuperar senha?",
                color = RentGoCores.Azul,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { mostrarRecuperarSenha = true }
                    .padding(6.dp)
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Não tem conta? ", color = RentGoCores.TextoCinza, fontSize = 15.sp)
                Text(
                    text = "Criar conta",
                    color = RentGoCores.Azul,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onCriarConta)
                        .padding(vertical = 6.dp, horizontal = 2.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (mostrarRecuperarSenha) {
        DialogoRecuperarSenha(
            emailInicial = email,
            onFechar = { mostrarRecuperarSenha = false }
        )
    }
}

@Composable
private fun DialogoRecuperarSenha(emailInicial: String, onFechar: () -> Unit) {
    val context = LocalContext.current
    var emailConta by remember { mutableStateOf(emailInicial) }
    var novaSenha by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf<String?>(null) }
    var concluido by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onFechar,
        containerColor = RentGoCores.Branco,
        title = { Text("Recuperar senha", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Informe o e-mail da conta e escolha uma nova senha.",
                    color = RentGoCores.TextoCinza,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = emailConta,
                    onValueChange = { emailConta = it; mensagem = null },
                    label = { Text("E-mail") },
                    singleLine = true,
                    enabled = !concluido,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = coresCampoRentGo()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = novaSenha,
                    onValueChange = { novaSenha = it; mensagem = null },
                    label = { Text("Nova senha") },
                    singleLine = true,
                    enabled = !concluido,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = coresCampoRentGo()
                )
                mensagem?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = if (concluido) RentGoCores.Verde else RentGoCores.Vermelho
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (concluido) {
                        onFechar()
                    } else if (novaSenha.length < 6) {
                        mensagem = "A senha precisa ter pelo menos 6 caracteres."
                    } else {
                        SessaoUsuario.redefinirSenha(context, emailConta, novaSenha) { deuCerto ->
                            if (deuCerto) {
                                concluido = true
                                mensagem = "Senha alterada! Já pode entrar."
                            } else {
                                mensagem = "Não existe conta com esse e-mail."
                            }
                        }
                    }
                }
            ) {
                Text(if (concluido) "OK" else "Salvar", color = RentGoCores.Azul)
            }
        },
        dismissButton = {
            if (!concluido) {
                TextButton(onClick = onFechar) {
                    Text("Cancelar", color = RentGoCores.TextoCinza)
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(onEntrar = {}, onCriarConta = {})
}

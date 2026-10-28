package com.example.rentgo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.data.SessaoUsuario
import com.example.rentgo.ui.components.AvatarIniciais
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun PerfilScreen(onSair: () -> Unit) {
    val usuario = SessaoUsuario.usuarioLogado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RentGoCores.Fundo)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Meu perfil",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = RentGoCores.TextoEscuro,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        AvatarIniciais(iniciais = usuario?.iniciais ?: "?", tamanho = 96.dp)

        Spacer(Modifier.height(16.dp))

        Text(
            text = usuario?.nome ?: "Visitante",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = RentGoCores.TextoEscuro
        )
        Text(
            text = usuario?.email ?: "",
            fontSize = 14.sp,
            color = RentGoCores.TextoCinza
        )

        Spacer(Modifier.height(36.dp))

        OutlinedButton(
            onClick = onSair,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, RentGoCores.Vermelho),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RentGoCores.Vermelho)
        ) {
            Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Sair da conta", fontWeight = FontWeight.SemiBold)
        }
    }
}

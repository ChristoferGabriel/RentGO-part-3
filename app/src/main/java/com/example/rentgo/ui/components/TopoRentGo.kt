package com.example.rentgo.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.rentgo.ui.theme.RentGoCores

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopoRentGo(
    titulo: String,
    onVoltar: () -> Unit,
    acoes: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(titulo, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = {
            IconButton(onClick = onVoltar) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Voltar")
            }
        },
        actions = acoes,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = RentGoCores.Fundo,
            titleContentColor = RentGoCores.TextoEscuro,
            navigationIconContentColor = RentGoCores.TextoEscuro,
            actionIconContentColor = RentGoCores.TextoEscuro
        )
    )
}

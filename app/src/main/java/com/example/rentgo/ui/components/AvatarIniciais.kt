package com.example.rentgo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun AvatarIniciais(
    iniciais: String,
    modifier: Modifier = Modifier,
    tamanho: Dp = 48.dp,
    onClick: (() -> Unit)? = null
) {
    val base = modifier
        .size(tamanho)
        .clip(CircleShape)
        .background(RentGoCores.Azul)

    Box(
        modifier = if (onClick != null) base.clickable(onClick = onClick) else base,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciais,
            color = RentGoCores.Branco,
            fontWeight = FontWeight.Bold,
            fontSize = (tamanho.value * 0.36f).sp
        )
    }
}

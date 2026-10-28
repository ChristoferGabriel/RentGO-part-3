package com.example.rentgo.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.ui.theme.RentGoCores

const val MIN_DIARIAS = 1
const val MAX_DIARIAS = 30

@Composable
fun SeletorDiarias(dias: Int, onMudar: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { onMudar(dias - 1) },
            enabled = dias > MIN_DIARIAS
        ) {
            Icon(Icons.Outlined.Remove, contentDescription = "Menos uma diária", tint = RentGoCores.Azul)
        }
        Text(
            text = "$dias",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = RentGoCores.TextoEscuro
        )
        Spacer(Modifier.width(4.dp))
        IconButton(
            onClick = { onMudar(dias + 1) },
            enabled = dias < MAX_DIARIAS
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Mais uma diária", tint = RentGoCores.Azul)
        }
    }
}

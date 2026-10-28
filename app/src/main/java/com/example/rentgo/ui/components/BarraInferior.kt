package com.example.rentgo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.navigation.Rotas
import com.example.rentgo.ui.theme.RentGoCores

private class ItemBarra(val rota: String, val rotulo: String, val icone: ImageVector)

private val itensBarra = listOf(
    ItemBarra(Rotas.HOME, "Início", Icons.Outlined.Home),
    ItemBarra(Rotas.CARROS, "Carros", Icons.Outlined.DirectionsCar),
    ItemBarra(Rotas.AGENDAMENTO, "Agendamento", Icons.Outlined.CalendarToday),
    ItemBarra(Rotas.FAVORITOS, "Favoritos", Icons.Outlined.FavoriteBorder),
    ItemBarra(Rotas.PERFIL, "Perfil", Icons.Outlined.Person)
)

@Composable
fun BarraInferior(rotaAtual: String?, onNavegar: (String) -> Unit) {
    Column {
        HorizontalDivider(thickness = 1.dp, color = RentGoCores.Borda)
        NavigationBar(containerColor = RentGoCores.Branco, tonalElevation = 0.dp) {
            itensBarra.forEach { item ->
                val central = item.rota == Rotas.AGENDAMENTO
                NavigationBarItem(
                    selected = rotaAtual == item.rota,
                    onClick = { onNavegar(item.rota) },
                    icon = {
                        if (central) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(RentGoCores.Azul),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icone,
                                    contentDescription = item.rotulo,
                                    tint = RentGoCores.Branco,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            Icon(item.icone, contentDescription = item.rotulo)
                        }
                    },
                    label = {
                        Text(
                            text = item.rotulo,
                            fontSize = 11.sp,
                            maxLines = 1,
                            softWrap = false,
                            fontWeight = if (central) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (central) RentGoCores.Azul else Color.Unspecified
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RentGoCores.Azul,
                        selectedTextColor = RentGoCores.Azul,
                        unselectedIconColor = RentGoCores.TextoCinza,
                        unselectedTextColor = RentGoCores.TextoCinza,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

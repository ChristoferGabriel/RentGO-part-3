package com.example.rentgo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun LogoRentGo(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                listOf(30.dp, 22.dp, 14.dp).forEach { largura ->
                    Box(
                        Modifier
                            .width(largura)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(RentGoCores.Azul)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(RentGoCores.Azul),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    tint = RentGoCores.Branco,
                    modifier = Modifier.size(46.dp)
                )
            }
            Spacer(Modifier.width(38.dp))
        }
        Text(
            text = "RentGo",
            fontSize = 46.sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            color = RentGoCores.Marinho
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(30.dp).height(1.dp).background(RentGoCores.Azul))
            Text(
                text = "  ALUGUE. ESCOLHA. VÁ.  ",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = RentGoCores.Azul
            )
            Box(Modifier.width(30.dp).height(1.dp).background(RentGoCores.Azul))
        }
    }
}

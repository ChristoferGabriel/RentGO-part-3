package com.example.rentgo.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.EvStation
import androidx.compose.material.icons.outlined.TimeToLeave
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rentgo.R
import com.example.rentgo.model.Carro
import com.example.rentgo.model.Categoria
import com.example.rentgo.ui.theme.RentGoCores

@DrawableRes
fun fotoDoCarro(carro: Carro): Int? = when {
    carro.nome.contains("focus", ignoreCase = true) -> R.drawable.ford_focus
    else -> null
}

private val IconeCamionete: ImageVector by lazy {
    ImageVector.Builder(
        name = "Camionete",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        // Carroceria: caçamba aberta atrás, cabine e capô na frente
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3.7f, 17f)
            horizontalLineTo(2f)
            verticalLineTo(10f)
            horizontalLineTo(10f)
            verticalLineTo(6f)
            horizontalLineTo(14.5f)
            lineTo(17.5f, 10f)
            lineTo(21.3f, 10.9f)
            quadTo(22f, 11.1f, 22f, 11.8f)
            verticalLineTo(17f)
            horizontalLineTo(19.3f)
            moveTo(8.3f, 17f)
            horizontalLineTo(14.7f)
        }
        // Janela da cabine
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.4f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(12.4f, 10f)
            verticalLineTo(7.9f)
            horizontalLineTo(13.9f)
            lineTo(15.3f, 10f)
            close()
        }
        // Roda de trás
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(3.7f, 17f)
            arcToRelative(2.3f, 2.3f, 0f, true, true, 4.6f, 0f)
            arcToRelative(2.3f, 2.3f, 0f, true, true, -4.6f, 0f)
            close()
        }
        // Roda da frente
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(14.7f, 17f)
            arcToRelative(2.3f, 2.3f, 0f, true, true, 4.6f, 0f)
            arcToRelative(2.3f, 2.3f, 0f, true, true, -4.6f, 0f)
            close()
        }
    }.build()
}

fun iconeDaCategoria(categoria: Categoria): ImageVector = when (categoria) {
    Categoria.SEDA -> Icons.Outlined.DirectionsCar
    Categoria.SUV -> Icons.Outlined.TimeToLeave
    Categoria.CAMIONETE -> IconeCamionete
    Categoria.ELETRICO -> Icons.Outlined.EvStation
}

@Composable
fun FotoCarro(
    carro: Carro,
    modifier: Modifier = Modifier,
    tamanhoIcone: Dp = 72.dp
) {
    val foto = fotoDoCarro(carro)
    if (foto != null) {
        Image(
            painter = painterResource(id = foto),
            contentDescription = carro.nome,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(
                Brush.verticalGradient(listOf(Color(0xFFDCE6FF), Color(0xFFF3F6FF)))
            ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconeDaCategoria(carro.categoria),
                contentDescription = carro.nome,
                tint = RentGoCores.Azul.copy(alpha = 0.55f),
                modifier = Modifier.size(tamanhoIcone)
            )
        }
    }
}
package com.example.rentgo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rentgo.ui.theme.RentGoCores

@Composable
fun coresCampoRentGo(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = RentGoCores.Branco,
    unfocusedContainerColor = RentGoCores.Branco,
    errorContainerColor = RentGoCores.Branco,
    focusedBorderColor = RentGoCores.Azul,
    unfocusedBorderColor = RentGoCores.Borda,
    focusedTextColor = RentGoCores.TextoEscuro,
    unfocusedTextColor = RentGoCores.TextoEscuro,
    cursorColor = RentGoCores.Azul,
    focusedLeadingIconColor = RentGoCores.TextoCinza,
    unfocusedLeadingIconColor = RentGoCores.TextoCinza,
    focusedTrailingIconColor = RentGoCores.TextoCinza,
    unfocusedTrailingIconColor = RentGoCores.TextoCinza,
    focusedPlaceholderColor = RentGoCores.Placeholder,
    unfocusedPlaceholderColor = RentGoCores.Placeholder
)

@Composable
fun CampoRentGo(
    rotulo: String,
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    icone: ImageVector,
    modifier: Modifier = Modifier,
    ehSenha: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    acaoTeclado: ImeAction = ImeAction.Next,
    onConcluir: () -> Unit = {}
) {
    var senhaVisivel by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = rotulo,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            color = RentGoCores.TextoCinza
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icone, contentDescription = null) },
            trailingIcon = if (ehSenha) {
                {
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(
                            imageVector = if (senhaVisivel) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (senhaVisivel) "Esconder senha" else "Mostrar senha"
                        )
                    }
                }
            } else null,
            visualTransformation = if (ehSenha && !senhaVisivel) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (ehSenha) KeyboardType.Password else tipoTeclado,
                imeAction = acaoTeclado
            ),
            keyboardActions = KeyboardActions(onDone = { onConcluir() }),
            colors = coresCampoRentGo()
        )
    }
}

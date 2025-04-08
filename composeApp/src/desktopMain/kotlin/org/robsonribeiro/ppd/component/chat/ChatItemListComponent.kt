package org.robsonribeiro.ppd.component.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import org.robsonribeiro.ppd.model.ChatMessage
import org.robsonribeiro.ppd.model.TypeMessage
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding

@Composable
fun ChatItemListComponent(
    chatMessage: ChatMessage,
    modifier: Modifier = Modifier
) {
    when(chatMessage.messageOwner) {
        TypeMessage.SYSTEM -> SystemTypeMessage(modifier, chatMessage)
        TypeMessage.OWNER -> OwnerTypeMessage(modifier, chatMessage)
        TypeMessage.FOREIGNER -> ForeignerTypeMessage(modifier, chatMessage)
    }
}


@Composable
fun OwnerTypeMessage(
    modifier: Modifier,
    chatMessage: ChatMessage
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.regular),
        horizontalAlignment =  Alignment.End
    ) {
        Card(
            modifier = modifier
                .wrapContentSize()
                .background(Color.Transparent)
                .padding(vertical = Padding.small),
            backgroundColor = Color.Transparent,
            elevation = Padding.none,
            shape = RoundedCornerShape(Padding.regular),
            border = BorderStroke(
                Padding.single, ColorResources.BaseBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .background(color = ColorResources.BlueCeltic)
                    .padding(Padding.regular)
            ) {
                Text(
                    text ="[${chatMessage.sender}]",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = ColorResources.White
                    )
                )
                Text(
                    text =chatMessage.message,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        color = ColorResources.White
                    )
                )
            }
        }
    }
}

@Composable
fun ForeignerTypeMessage(
    modifier: Modifier,
    chatMessage: ChatMessage
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.regular),
        horizontalAlignment =  Alignment.Start
    ) {
        Card(
            modifier = modifier
                .wrapContentSize()
                .background(Color.Transparent)
                .padding(vertical = Padding.small),
            backgroundColor = Color.Transparent,
            elevation = Padding.none,
            shape = RoundedCornerShape(Padding.regular),
            border = BorderStroke(
                Padding.single, ColorResources.BaseBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .background(color = ColorResources.BlueRoyalDarker)
                    .padding(Padding.regular)
            ) {
                Text(
                    text ="[${chatMessage.sender}]",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = ColorResources.White
                    )
                )
                Text(
                    text =chatMessage.message,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        color = ColorResources.White
                    )
                )
            }
        }
    }
}

@Composable
fun SystemTypeMessage(
    modifier: Modifier,
    chatMessage: ChatMessage
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.regular),
        horizontalAlignment =  Alignment.Start
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(vertical = Padding.small),
            backgroundColor = Color.Transparent,
            elevation = Padding.none,
            shape = RoundedCornerShape(Padding.regular),
            border = BorderStroke(
                Padding.single, ColorResources.BaseBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .background(color = ColorResources.BaseBackground)
                    .padding(Padding.regular),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text =chatMessage.message,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        color = ColorResources.BlackRich
                    )
                )
            }
        }
    }
}
package org.robsonribeiro.ppd.component.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import org.robsonribeiro.ppd.model.ChatMessage
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.StringResources
import org.robsonribeiro.ppd.values.TextSize

@Composable
fun ChatComponent(
    modifier: Modifier,
    messages: List<ChatMessage>,
    sendMessage: (String)->Unit
) {
    Column (modifier = modifier.fillMaxSize()) {
        ChatHeader(Modifier.fillMaxWidth())
        ChatListComponent(
            modifier = Modifier.weight(1f),
            messages = messages
        )
        ChatTextFieldComponent(
            modifier = Modifier.fillMaxWidth(),
            onSend = sendMessage
        )
    }
}

@Composable
fun ChatHeader(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(top = Padding.regular),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Padding.regular)
    ) {
        Text(
            text = StringResources.CHAT_HEADER,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                lineHeight = TextSize.large,
                fontSize = TextSize.large,
                color = ColorResources.BlackRich
            )
        )
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = ColorResources.BaseBackground,
            thickness = Padding.single
        )
    }
}
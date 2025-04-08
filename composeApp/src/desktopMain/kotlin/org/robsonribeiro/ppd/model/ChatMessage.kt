package org.robsonribeiro.ppd.model

data class ChatMessage(
    val sender: String,
    val message: String,
    val messageOwner: TypeMessage = TypeMessage.FOREIGNER
)

enum class TypeMessage {
    SYSTEM, OWNER, FOREIGNER
}


val MOCK_CHAT_MESSAGE = List(30) { it: Int ->
    val isOdd = it%2!=0
    ChatMessage(
        "sender #$isOdd",
        "lorem impsum #$it, fofjfognsfoggsdfognfdognfdogdfngofdgndfgfdgdg",
        messageOwner = if (isOdd) TypeMessage.OWNER else TypeMessage.FOREIGNER
    )
}
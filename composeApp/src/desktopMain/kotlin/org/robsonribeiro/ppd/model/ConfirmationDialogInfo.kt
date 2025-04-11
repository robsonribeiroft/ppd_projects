package org.robsonribeiro.ppd.model

data class ConfirmationDialogInfo(
    val title: String,
    val description: String,
    val onDismiss: ()->Unit = {},
    val onConfirm: ()->Unit = {}
)

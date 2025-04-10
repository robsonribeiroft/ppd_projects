package org.robsonribeiro.ppd.values

val String.Companion.empty get() = ""

object StringResources {

    const val APPLICATION_NAME = "PPD"

    const val CHAT_HEADER = "Chat"
    const val CHAT_TEXT_FIELD_HINT = "Write a message"

    const val SERVER_BUTTON_SERVER_DISABLED = "Server is disabled"
    const val SERVER_BUTTON_SERVER_RUNNING = "Server is running"
    const val SERVER_BUTTON_CLICK_SETUP = "Click here to setup"


    const val INFO_DIALOG_TITLE_JOIN_WITH_DISABLED_SERVER = "Not possible to Join"
    const val INFO_DIALOG_DESCRIPTION_JOIN_WITH_DISABLED_SERVER = "Before join, you must initialize the server!"
}
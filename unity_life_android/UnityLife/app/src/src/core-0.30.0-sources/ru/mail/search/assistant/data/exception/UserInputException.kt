package ru.mail.search.assistant.data.exception

import java.net.UnknownHostException

class UserInputException(override val cause: UnknownHostException) : RuntimeException()
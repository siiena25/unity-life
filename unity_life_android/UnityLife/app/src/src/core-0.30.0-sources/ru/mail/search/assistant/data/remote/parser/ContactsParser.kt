package ru.mail.search.assistant.data.remote.parser

import android.Manifest
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import ru.mail.search.assistant.common.data.exception.parsingError
import ru.mail.search.assistant.common.ui.PermissionManager
import ru.mail.search.assistant.common.util.getArray
import ru.mail.search.assistant.common.util.getBoolean
import ru.mail.search.assistant.common.util.getInt
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.entities.ServerCommand

internal class ContactsParser {

    companion object {
        const val ADDRESS_BOOK_PERMISSION_CARD = "address_book_permission_request"
        const val ADDRESS_BOOK_PERMISSION_REQUEST = "address_book_permission_request_native"
        const val ADDRESS_BOOK_REQUEST = "address_book_request"
        const val MATCHED_CONTACT_CARD = "matched_contact_card"
        const val PHONE_CALL = "phone_call"
        const val PHONE_CARD = "phone_card"
        const val EMERGENCY_CALL = "emergency_call"
        const val EMERGENCY_CARD = "emergency_card"
        const val MATCHED_CONTACT_PHONES_CARD = "matched_contact_phones_card"
        const val CHECKING_PHONE_BY_DIGITS = "checking_phone_by_digits"
        const val EDIT_MESSAGE = "edit_message"
        const val SEND_SMS = "send_sms"

        private const val CALLBACK_EVENT_KEY = "callback_event"

        private const val CALLS_PERMISSIONS_REQUESTED = "phone_calls_address_book_permission_requested"
        private const val SMS_PERMISSIONS_REQUESTED = "sms_address_book_permission_requested"
    }

    fun parse(commandJson: JsonObject): ServerCommand = with(commandJson) {
        return when (val type = getString("type")) {
            ADDRESS_BOOK_PERMISSION_CARD -> mapToPermissionCard()
            ADDRESS_BOOK_PERMISSION_REQUEST -> mapToPermissionRequest()
            ADDRESS_BOOK_REQUEST -> mapToContactsSync()
            MATCHED_CONTACT_CARD -> mapToContactsCard()
            MATCHED_CONTACT_PHONES_CARD -> mapToContactNumbersCard()
            CHECKING_PHONE_BY_DIGITS -> mapToFindPhoneByPart()
            PHONE_CALL -> mapToPhoneCall()
            PHONE_CARD -> mapToPhoneCard()
            EMERGENCY_CALL -> mapToEmergencyCall()
            EMERGENCY_CARD -> mapToEmergencyCard()
            EDIT_MESSAGE -> mapToEditSmsCommand()
            SEND_SMS -> mapToSendSmsCommand()
            else -> parsingError("$type: unknown type")
        }
    }

    private fun JsonObject.mapToPermissionCard(): ServerCommand {
        val text = getString("card_text")
            ?: parsingError("$ADDRESS_BOOK_PERMISSION_CARD: missing text")
        return ServerCommand.PermissionRequestCard(text)
    }

    private fun JsonObject.mapToPermissionRequest(): ServerCommand {
        val requestCode = when (parseCallbackEvent(type = ADDRESS_BOOK_PERMISSION_REQUEST)) {
            CALLS_PERMISSIONS_REQUESTED -> PermissionManager.CALLS_CONTACTS_PERMISSION_REQUEST_CODE
            SMS_PERMISSIONS_REQUESTED -> PermissionManager.SMS_CONTACTS_PERMISSION_REQUEST_CODE
            else -> parsingError("unknown callback event")
        }
        return ServerCommand.PermissionRequestCommand(
            permission = Manifest.permission.READ_CONTACTS,
            requestCode = requestCode
        )
    }

    private fun JsonObject.mapToContactsSync(): ServerCommand {
        val event = parseCallbackEvent(type = ADDRESS_BOOK_REQUEST)
        return ServerCommand.ContactsSync(event)
    }

    private fun JsonObject.mapToContactsCard(): ServerCommand {
        val contacts = getArray("contacts")
            ?.map(JsonElement::getAsInt)
            ?: parsingError("$MATCHED_CONTACT_CARD: missing page contacts")

        val page = getInt(name = "page") ?: parsingError("$MATCHED_CONTACT_CARD: missing page field")

        return ServerCommand.ContactsCard(
            hasMore = getBoolean(name = "has_more", default = false),
            callbackEvent = parseCallbackEvent(type = MATCHED_CONTACT_CARD),
            page = page,
            contacts = contacts,
        )
    }

    private fun JsonObject.mapToContactNumbersCard(): ServerCommand {
        val contactId = parseContactId(MATCHED_CONTACT_PHONES_CARD)
        val callbackEvent = parseCallbackEvent(MATCHED_CONTACT_PHONES_CARD)
        return ServerCommand.ContactNumbersCard(contactId, callbackEvent)
    }

    private fun JsonObject.mapToFindPhoneByPart(): ServerCommand {
        val contactId = parseContactId(CHECKING_PHONE_BY_DIGITS)
        val phoneDigits = getString("phone_digits")
            ?: parsingError("$CHECKING_PHONE_BY_DIGITS: missing phone_digits field")
        val callbackEvent = parseCallbackEvent(CHECKING_PHONE_BY_DIGITS)
        return ServerCommand.FindPhoneByPart(contactId, phoneDigits, callbackEvent)
    }

    private fun JsonObject.mapToPhoneCall(): ServerCommand {
        val numberId = parsePhoneNumberId(type = PHONE_CALL)
        return ServerCommand.PhoneCall(numberId)
    }

    private fun JsonObject.mapToPhoneCard(): ServerCommand {
        val numberId = parsePhoneNumberId(type = PHONE_CARD)
        val contactId = parseContactId(PHONE_CARD)
        return ServerCommand.PhoneCallCard(contactId, numberId)
    }

    private fun JsonObject.mapToEmergencyCall(): ServerCommand {
        val phoneNumber = parseEmergencyPhone(type = EMERGENCY_CALL)
        return ServerCommand.EmergencyCall(phoneNumber)
    }

    private fun JsonObject.mapToEmergencyCard(): ServerCommand {
        val phoneNumber = parseEmergencyPhone(type = EMERGENCY_CARD)
        return ServerCommand.EmergencyCallCard(phoneNumber)
    }

    private fun JsonObject.mapToEditSmsCommand(): ServerCommand = ServerCommand.EditSms(text = parseText())

    private fun JsonObject.mapToSendSmsCommand(): ServerCommand {
        val numberId = getInt("phone_id") ?: parsingError("$SEND_SMS: missing phone_id field")
        return ServerCommand.SendSms(numberId, text = parseText())
    }

    private fun JsonObject.parseText(): String = getString("text").orEmpty()

    private fun JsonObject.parseContactId(type: String): Int {
        return getInt("contact_id")
            ?: parsingError("$type: missing contact_id field")
    }

    private fun JsonObject.parsePhoneNumberId(type: String): Int {
        return getInt("phone_id") ?: parsingError("$type: missing phone_id field")
    }

    private fun JsonObject.parseEmergencyPhone(type: String): String {
        return getString("emergency_phone")
            ?: parsingError("$type: missing emergency_phone field")
    }

    private fun JsonObject.parseCallbackEvent(type: String): String {
        return getString(CALLBACK_EVENT_KEY)
            ?: parsingError("$type: missing $CALLBACK_EVENT_KEY field")
    }
}
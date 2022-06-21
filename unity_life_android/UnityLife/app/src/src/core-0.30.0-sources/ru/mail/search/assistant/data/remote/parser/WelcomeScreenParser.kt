package ru.mail.search.assistant.data.remote.parser

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.JsonObject
import ru.mail.search.assistant.api.suggests.SuggestsParser
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.data.remote.dto.dashboard.DashboardCurrencyDto
import ru.mail.search.assistant.data.remote.dto.dashboard.DashboardItemsDto
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.message.welcome.WelcomeScreen
import ru.mail.search.assistant.entities.message.welcome.WelcomeShortcut
import ru.mail.search.assistant.entities.message.welcome.WelcomeShortcutPromo
import ru.mail.search.assistant.entities.message.welcome.WelcomeWeather
import ru.mail.search.assistant.entities.message.widgets.WidgetsCurrency
import ru.mail.search.assistant.util.Tag
import java.text.SimpleDateFormat
import java.util.*

internal class WelcomeScreenParser(
    private val gson: Gson,
    private val suggestsParser: SuggestsParser,
    private val logger: Logger?
) {

    private val locale = Locale("ru")
    private val dateFormat = SimpleDateFormat("EEEE, d ", locale)
    private val monthFormat = SimpleDateFormat("MMMM", locale)

    fun parse(commandJson: JsonObject): ServerCommand {
        return commandJson.get("items")?.toString()
            ?.let { json -> gson.fromJson(json, DashboardItemsDto::class.java) }
            ?.let { dto ->
                val dashboard = WelcomeScreen(
                    title = dto.greeting?.text,
                    subtitle = getSubtitle(),
                    weather = dto.weather
                        ?.takeIf { weatherDto -> weatherDto.temperature != null }
                        ?.let { weatherDto ->
                            WelcomeWeather(
                                temperature = weatherDto.temperature ?: 0,
                                subtitle = weatherDto.conditions,
                                iconUrl = weatherDto.iconUrl
                            )
                        },
                    currencies = dto.currencies?.currencies
                        ?.mapNotNull(::parseCurrency)
                        .orEmpty(),
                    miniCards = emptyList(),
                    shortcuts = dto.shortcuts
                        ?.map(::parseShortcut)
                        .orEmpty()
                )
                ServerCommand.WelcomeScreenCommand(dashboard)
            }
            ?: throw ResultParsingException("Welcome screen: missing items field")
    }

    @SuppressLint("DefaultLocale")
    private fun getSubtitle(): String {
        val date = Date()
        return dateFormat.format(date).capitalize() + monthFormat.format(date).toLowerCase(locale)
    }

    private fun parseCurrency(dto: DashboardCurrencyDto): WidgetsCurrency? {
        val code = dto.code
        val symbol = dto.symbol
        val rate = dto.rate
        val movement = when (dto.movement) {
            "up" -> WidgetsCurrency.Movement.UPWARD
            "down" -> WidgetsCurrency.Movement.DOWNWARD
            else -> null
        }
        if (rate == null) {
            logger?.e(Tag.DASHBOARD, ResultParsingException("Missing currency rate"))
            return null
        }
        if (symbol.isNullOrEmpty()) {
            logger?.e(
                Tag.DASHBOARD,
                ResultParsingException("Failed to parse currency symbol: $symbol")
            )
            return null
        }
        return WidgetsCurrency(
            code = code,
            symbol = symbol,
            rate = rate,
            movement = movement
        )
    }

    private fun parseShortcut(json: JsonObject): WelcomeShortcut {
        return suggestsParser.parseSuggest(json)?.let { suggest ->
            val icons = json.getObject("icons")
            WelcomeShortcut(
                title = json.getString("title"),
                suggest = suggest,
                lightIconUrl = icons?.getString("light"),
                darkIconUrl = icons?.getString("dark"),
                promo = json.getObject("promo")
                    ?.takeIf { promoJson ->
                        promoJson.getString("gradient") != null
                    }
                    ?.let {
                        WelcomeShortcutPromo(icons?.getString("promo"))
                    }
            )
        }
            ?: throw ResultParsingException("Welcome screen: missing suggest")
    }
}
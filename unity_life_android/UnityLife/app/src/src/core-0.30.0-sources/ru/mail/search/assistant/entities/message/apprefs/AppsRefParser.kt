package ru.mail.search.assistant.entities.message.apprefs

class AppsRefParser {

    private companion object {
        private const val REF_MATCH_GROUP_SIZE = 4
        private const val RAW_REFERENCE = 0
        private const val REF_NAME = 1
        private const val APP_TITLE = 3
    }

    private val regex = Regex("""\$(.*?)(\[(.*?)\])""")

    fun <T> parse(
        text: String,
        appRefs: List<T>,
        refTypeProvider: (T) -> String,
    ): Pair<String, List<ReferenceSpan<T>>> {
        val matchGroups = regex.findAll(text)
        var replacedText = text

        val spans = matchGroups
            .map { it.groups }
            .mapNotNull { group ->
                if (group.size != REF_MATCH_GROUP_SIZE) {
                    throw IllegalArgumentException("Wrong applications references")
                } else {
                    val result = replaceText(replacedText, group)
                    replacedText = result.first
                    val lastIndex = result.second
                    toSpan(replacedText, lastIndex, appRefs, group, refTypeProvider)
                }
            }.toList()

        return Pair(replacedText, spans)
    }

    private fun <T> toSpan(
        replacedText: String,
        lastIndex: Int,
        appRefs: List<T>,
        group: MatchGroupCollection,
        refTypeProvider: (T) -> String,
    ): ReferenceSpan<T> {
        val refName = group[REF_NAME]
        val title = group[APP_TITLE]
            ?: throw IllegalArgumentException("Application title is empty")

        val start = replacedText.indexOf(title.value, startIndex = lastIndex)
        val end = start + title.value.length

        val appRef = appRefs.find { refTypeProvider(it) == refName?.value }
            ?: throw IllegalArgumentException("Couldn't find app reference for ${refName?.value}")

        return ReferenceSpan(appRef, start, end)
    }

    private fun replaceText(replacedText: String, group: MatchGroupCollection): Pair<String, Int> {
        val replace = group[RAW_REFERENCE] ?: return Pair(replacedText, 0)
        val title = group[APP_TITLE] ?: return Pair(replacedText, 0)
        val index = replacedText.indexOf(replace.value)

        return Pair(replacedText.replaceFirst(replace.value, title.value), index)
    }
}
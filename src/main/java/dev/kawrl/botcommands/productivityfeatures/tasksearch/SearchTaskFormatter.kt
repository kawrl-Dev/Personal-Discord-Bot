package dev.kawrl.botcommands.productivityfeatures.tasksearch

import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button

const val page_Size = 5

data class SearchPage(
    val content: String,
    val components: List<ActionRow>
)

fun buildSearchPage(
    results: List<String>,
    keyword: String,
    page: Int,
    totalResults: Int
): SearchPage {
    val totalPages = Math.ceilDiv(totalResults,page_Size)
    val stringBuilder = StringBuilder()

    stringBuilder.append("🔍 Results for **\"$keyword\"** -- Page ${page + 1}/$totalPages ($totalResults total)\n\n")

    results.forEachIndexed { index, line ->
        stringBuilder.append("${page * page_Size + index + 1}. $line\n")
    }

    val buttons = mutableListOf<Button>()

    if (page > 0) buttons.add(Button.primary("search-page:${page - 1}|$keyword","◀️  Back"))

    if ((page + 1) * page_Size < totalResults) buttons.add(Button.primary("search-page:${page + 1}|$keyword","️Next  ▶️"))

    val components = if (buttons.isNotEmpty())
        listOf(ActionRow.of(buttons))
    else emptyList()

    return SearchPage(stringBuilder.toString(),components)
}
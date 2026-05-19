package dev.kawrl.botcommands.productivityfeatures.tasksearch

import dev.kawrl.database.TaskRepo
import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.TaskRepositoryInterface
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class SearchTaskPageHandler(private val repo: TaskRepositoryInterface) : CommandHandler(), CommandHandler.ButtonInterface {
    override fun handle(event: ButtonInteractionEvent) {
        val member = event.member?: return

        // componentId format: "search-page:<pageIndex>|<keyword>"
        val afterPrefix = event.componentId.removePrefix("search-page:")
        val pipeIndex = afterPrefix.indexOf('|')
        if (pipeIndex == -1) {
            logger.warn("Malformed search-page button ID: '{}'", event.componentId)
            return
        }

        val page = afterPrefix.substring(0, pipeIndex).toIntOrNull() ?: run {
            logger.warn("Non-integer page index in button ID: '{}'", event.componentId)
            return
        }
        val keyword = afterPrefix.substring(pipeIndex + 1)
        val userId = member.id

        try {
            val total = repo.countSearchResults(userId, keyword)
            val results = repo.searchTasks(userId, keyword, page * page_Size, page_Size)
            val built = buildSearchPage(results, keyword, page, total)

            // editMessage replaces the existing ephemeral reply in-place
            event.editMessage(built.content)
                .setComponents(built.components)
                .queue()

        } catch (e: Exception) {
            logger.error("DB error during page turn for user '{}': {}", member.user.name, e.toString())
            event.reply("A database error occurred. Please try again later.")
                .setEphemeral(true)
                .queue()
        }
    }
}
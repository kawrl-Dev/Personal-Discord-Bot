package dev.kawrl.botcommands.productivityfeatures.tasksearch

import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.TaskRepositoryInterface
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import java.sql.SQLException

class SearchTaskSubmissionHandler(private val repo: TaskRepositoryInterface) : CommandHandler(), CommandHandler.ModalInterface {
    override fun execute(event: ModalInteractionEvent) {
        val member = event.member?: return
        val userID = member.id
        val keyword = event.getValue("search_keyword")?.asString?.trim() ?: return

        try {
            val total = repo.countSearchResults(userID,keyword)

            if (total != 0){
                val results = repo.searchTasks(userID,keyword,0,page_Size)
                val page = buildSearchPage(results,keyword,0,total)

                event.reply(page.content)
                    .addComponents(page.components)
                    .setEphemeral(true)
                    .queue()
            }

            else event.reply("No tasks found matching **\"$keyword\"**.")
                .setEphemeral(true)
                .queue()
        }
        catch (e: SQLException){
            logger.error("DB error during task search for user '{}': {}",member.user.name,e.toString())
            event.reply("A database error occured. Please try again later.")
                .setEphemeral(true)
                .queue()
        }
    }
}
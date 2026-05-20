package dev.kawrl.botcommands.productivityfeatures.taskdisplay

import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.StringSelectMenuInterface
import dev.kawrl.interfaces.TaskRepositoryInterface
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import java.sql.SQLException

class ViewListHandler(private val repo: TaskRepositoryInterface) : CommandHandler(), StringSelectMenuInterface {
    override fun handle(event: StringSelectInteractionEvent) {
        val listID = event.values[0].toLong()
        val listName = event.selectedOptions[0].label

        try {
            val formatted = repo.formatTaskList(listID,listName)
            event.reply(formatted).setEphemeral(true).queue()
        } catch (e: SQLException){
            logger.error("Database error while viewing list #{}: {}", listID, e.toString())
            event.hook.editOriginal("A database error occurred. Please try again later.").queue()
        }
    }
}
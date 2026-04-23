package dev.kawrl.botcommands.productivityfeatures.taskdisplay

import dev.kawrl.database.TaskRepo
import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.StringSelectMenuInterface
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import java.sql.SQLException

class ViewListHandler : CommandHandler(), StringSelectMenuInterface {
    override fun handle(event: StringSelectInteractionEvent) {
        val listID = event.values[0].toLong()
        val listname = event.selectedOptions[0].label

        try {
            val formatted = TaskRepo.formatTaskList(listID,listname)
            event.reply(formatted).setEphemeral(true).queue()
        } catch (e: SQLException){
            logger.error("Database error while viewing list #{}: {}", listID, e.toString())
            event.reply("A database error occurred. Please try again later.").setEphemeral(true).queue()
        }
    }
}
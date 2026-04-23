package dev.kawrl.botcommands.productivityfeatures.taskdisplay

import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.sql.SQLException

class ViewListCommand: CommandHandler(), CommandHandler.SlashCommandInterface {
    override fun execute(event: SlashCommandInteractionEvent?) {
        val member = event!!.member?: return
        val username = member.user.name

        try {
            replyWithListSelector(
                event,
                "select-list:view-list",
                "Which list would you like to view?"
            )
        } catch (e: SQLException){
            logger.error("Database error while getting task lists from user '{}': {}", username, e.toString())
            event.reply("A database error occurred. Please try again later.")
                .setEphemeral(true)
                .queue()
        }
    }
}
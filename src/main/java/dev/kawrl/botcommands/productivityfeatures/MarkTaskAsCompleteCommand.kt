package dev.kawrl.botcommands.productivityfeatures

import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.SlashCommandInterface
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.sql.SQLException

class MarkTasksAsCompleteCommand : CommandHandler(), SlashCommandInterface {
    override fun execute(event: SlashCommandInteractionEvent) {
        val member = event.member
        if (member == null) return
        val username = member.user.name

        try {
            replyWithListSelector(
                event,
                "select-list:mark-task",
                "Which list would you like to mark tasks in?"
            )
        } catch (e: SQLException) {
            logger.error("Database error while getting task lists from user '{}': {}", username, e.toString())
            event.reply("A database error occurred. Please try again later.")
                .setEphemeral(true)
                .queue()
        }
    }
}
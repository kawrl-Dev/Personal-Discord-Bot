package dev.kawrl.botcommands.productivityfeatures.taskcreation

import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.SlashCommandInterface
import dev.kawrl.interfaces.TaskRepositoryInterface
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.sql.SQLException

class AddTaskCommand(private val repo: TaskRepositoryInterface) : CommandHandler(), SlashCommandInterface {
    override fun execute(event: SlashCommandInteractionEvent) {
        val member = event.member

        if (member == null) return

        val username = member.user.name

        try {
            replyWithListSelector(
                event,
                "select-list:add-task",
                "Which list would you like to add a task to?",
                repo
            )
        } catch (e: SQLException) {
            logger.error("Database error while getting task lists from user '{}': {}", username, e.toString())
            event.hook.editOriginal("A database error occurred. Please try again later.")
                .queue()
        }
    }
}
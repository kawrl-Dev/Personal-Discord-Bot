package dev.kawrl.botcommands.productivityfeatures

import dev.kawrl.database.TaskRepo
import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.SlashCommandInterface
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.sql.SQLException

class CreateNewTaskListCommand : CommandHandler(), SlashCommandInterface {
    override fun execute(event: SlashCommandInteractionEvent) {
        val member = event.member
        val option = event.getOption("list-name")

        if (option == null || member == null) return

        val listName = option.asString
        val username = member.user.name
        val userId = member.id

        try {
            // Ensure the user row exists before inserting the list (FK constraint)
            TaskRepo.upsertUser(userId, username)

            if (TaskRepo.listExistsForUser(userId, listName)) {
                event.reply(String.format("You already have a task list named **%s**!", listName))
                    .setEphemeral(true)
                    .queue()
                logger.warn("User '{}' tried to create a duplicate task list: '{}'", username, listName)
                return
            }

            val listId = TaskRepo.createTaskList(userId, listName)
            logger.info("New task list '{}' (id={}) created for user '{}'", listName, listId, username)
            event.reply(String.format("**New Task List Created!** Name: *%s*", listName))
                .setEphemeral(true)
                .queue()
        } catch (e: SQLException) {
            logger.error("Database error while creating task list for user '{}': {}", username, e.toString())
            event.reply("A database error occurred. Please try again later.")
                .setEphemeral(true)
                .queue()
        }
    }
}
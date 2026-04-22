package dev.kawrl.botcommands.productivityfeatures

import dev.kawrl.database.TaskRepo
import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.SlashCommandInterface
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.sql.SQLException

class AddTaskCommand : CommandHandler(), SlashCommandInterface {
    override fun execute(event: SlashCommandInteractionEvent) {
        val member = event.member

        if (member == null) return

        val username = member.user.name

        try {
            val lists = TaskRepo.getListNamesForUser(member.id)

            if (lists.isEmpty()) {
                event.reply("You have no task lists yet! Create one first with '/create-list'")
                    .setEphemeral(true)
                    .queue()
                return
            }

            val menuBuilder = StringSelectMenu.create("select-list").setPlaceholder("Choose a Task List")
            lists.forEach { (listName: String?, listId: Long?) ->
                menuBuilder.addOption(
                    listName!!,
                    listId.toString()
                )
            }

            event.reply("Which list would you like to add a task to?")
                .addComponents(ActionRow.of(menuBuilder.build()))
                .setEphemeral(true)
                .queue()
        } catch (e: SQLException) {
            logger.error("Database error while getting task lists from user '{}': {}", username, e.toString())
            event.reply("A database error occurred. Please try again later.")
                .setEphemeral(true)
                .queue()
        }
    }
}
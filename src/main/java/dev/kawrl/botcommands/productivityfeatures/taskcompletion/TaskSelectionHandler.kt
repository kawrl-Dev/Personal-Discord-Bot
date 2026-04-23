package dev.kawrl.botcommands.productivityfeatures.taskcompletion

import dev.kawrl.database.TaskRepo
import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.StringSelectMenuInterface
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import java.sql.SQLException

class TaskSelectionHandler : CommandHandler(), StringSelectMenuInterface {
    override fun handle(event: StringSelectInteractionEvent) {
        val member = event.member
        val username = member!!.user.name
        val listId: String = event.values[0]
        try {
            val taskList = TaskRepo.getTasksFromTaskListForUser(listId,event.user.id)

            if (!taskList.isEmpty()){
                val taskSelectMenu: StringSelectMenu.Builder = StringSelectMenu.create("approve-selected-tasks:$listId")
                    .setPlaceholder("Choose One or Multiple Tasks")
                    .setRequiredRange(1,(taskList.size))

                taskList.forEach { task, id -> (taskSelectMenu.addOption(task,id.toString())) }
                event.reply("Select the task items that will be marked as finished").addComponents(
                    ActionRow.of(taskSelectMenu.build()))
                    .setEphemeral(true)
                    .queue()
            }
            else{
                event.reply("You have no tasks in this list yet! Create one first with '/add-task'")
                    .setEphemeral(true)
                    .queue()
            }
        } catch (e: SQLException){
            logger.warn("Database error while getting task lists from user '{}': {}",username, e.toString())
            event.reply("A database error occurred. Please try again later.")
                .setEphemeral(true)
                .queue()
        }
    }
}
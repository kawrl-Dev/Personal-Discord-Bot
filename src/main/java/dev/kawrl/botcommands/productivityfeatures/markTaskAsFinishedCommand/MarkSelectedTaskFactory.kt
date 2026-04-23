package dev.kawrl.botcommands.productivityfeatures.markTaskAsFinishedCommand

import dev.kawrl.database.TaskRepo
import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.StringSelectMenuInterface
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class MarkSelectedTaskFactory : CommandHandler(), StringSelectMenuInterface {
    override fun handle(event: StringSelectInteractionEvent) {
        val listId: String = event.values[0]
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
    }
}
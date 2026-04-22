package dev.kawrl.botcommands.productivityfeatures.markTaskAsFinishedCommand

import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.StringSelectMenuInterface
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class MarkSelectedTaskFactory : CommandHandler(), StringSelectMenuInterface {
    override fun handle(event: StringSelectInteractionEvent) {
        val listId: String = event.values[0]

        val taskSelectMenu: StringSelectMenu.Builder = StringSelectMenu.create("select-task")
            .setPlaceholder("Choose One or Multiple Tasks")

        event.reply("Select the task items that will be marked as finished").addComponents(
            ActionRow.of(taskSelectMenu.build()))
            .setEphemeral(true)
            .queue()
    }
}
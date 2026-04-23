package dev.kawrl.botcommands.productivityfeatures.taskcompletion

import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class MarkTaskConfirmation : CommandHandler(), CommandHandler.StringSelectMenuInterface {
    override fun handle(event: StringSelectInteractionEvent) {
        val listID = event.componentId.removePrefix("approve-selected-tasks:")
        val encodedIDs: String = event.values.joinToString(",")

        val yesButton: Button = Button.success("yes-response:$listID|$encodedIDs", "All good!")
        val noButton: Button = Button.danger("no-response", "Actually, nevermind!")

        event.reply("Do you want to mark the selected tasks as Finished?")
            .addComponents(ActionRow.of(yesButton, noButton))
            .setEphemeral(true)
            .queue()
    }
}
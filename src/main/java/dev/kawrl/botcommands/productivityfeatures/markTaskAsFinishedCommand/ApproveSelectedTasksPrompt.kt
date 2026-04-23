package dev.kawrl.botcommands.productivityfeatures.markTaskAsFinishedCommand

import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class ApproveSelectedTasksPrompt: CommandHandler(), CommandHandler.StringSelectMenuInterface {
    override fun handle(event: StringSelectInteractionEvent) {
        val encodedIDs: String = event.values.joinToString(",")

        val yesButton: Button = Button.success("yes-response:$encodedIDs","All good!")
        val noButton: Button = Button.danger("no-response","Actually, nevermind!")

        event.reply("Do you want to mark the selected tasks as Finished?")
            .addComponents(ActionRow.of(yesButton,noButton))
            .setEphemeral(true)
            .queue()
    }
}
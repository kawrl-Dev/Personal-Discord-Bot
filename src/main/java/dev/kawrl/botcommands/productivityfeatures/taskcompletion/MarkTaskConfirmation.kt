package dev.kawrl.botcommands.productivityfeatures.taskcompletion

import dev.kawrl.interfaces.ButtonSpec
import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class MarkTaskConfirmation : CommandHandler(), CommandHandler.StringSelectMenuInterface {
    override fun handle(event: StringSelectInteractionEvent) {
        val listID = event.componentId.removePrefix("approve-selected-tasks:")
        val encodedIDs: String = event.values.joinToString(",")

        replyWithConfirmation(
            event::reply,
            ButtonSpec("yes-response:$listID|$encodedIDs","All good!"),
            ButtonSpec("no-response","Actually, nevermind!"),
            "Do you want to mark the selected tasks as Finished?"
        )

        logger.info("User '{}' was prompted to confirm marking selected tasks as Finished.",event.member!!.user.name)
    }
}
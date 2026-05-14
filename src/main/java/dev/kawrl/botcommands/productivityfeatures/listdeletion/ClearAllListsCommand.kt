package dev.kawrl.botcommands.productivityfeatures.listdeletion

import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ClearAllListsCommand : CommandHandler(), CommandHandler.SlashCommandInterface{
    override fun execute(event: SlashCommandInteractionEvent?) {
        val member = event?.member?: return

        replyWithConfirmation(
            event,
            "confirm-clear-yes",
            "confirm-clear-no",
            "⚠️ Are you sure you want to delete **ALL** your task lists? **This cannot be undone.**"
        )

        logger.info("User '{}' was prompted to confirm clearing all lists.",member.user.name)
    }
}
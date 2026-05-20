package dev.kawrl.botcommands.productivityfeatures.listdeletion

import dev.kawrl.interfaces.ButtonSpec
import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ClearAllListsCommand : CommandHandler(), CommandHandler.SlashCommandInterface{
    override fun execute(event: SlashCommandInteractionEvent?) {
        val member = event?.member?: return

        replyWithConfirmation(
            event::reply,
            ButtonSpec("confirm-clear-yes", "I know what I'm doing."),
            ButtonSpec("confirm-clear-no","Cancel List Deletion"),
            "⚠️ Are you sure you want to delete **ALL** your task lists? **This cannot be undone.**"
        )

        logger.info("User '{}' was prompted to confirm clearing all lists.",member.user.name)
    }
}
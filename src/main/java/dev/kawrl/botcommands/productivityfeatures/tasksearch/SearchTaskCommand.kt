package dev.kawrl.botcommands.productivityfeatures.tasksearch

import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class SearchTaskCommand: CommandHandler(), CommandHandler.SlashCommandInterface {
    override fun execute(event: SlashCommandInteractionEvent) {
        event.replyModal(SearchTaskModal.build()).queue()
    }
}
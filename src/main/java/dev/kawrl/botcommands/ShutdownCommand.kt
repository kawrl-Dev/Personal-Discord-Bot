package dev.kawrl.botcommands

import dev.kawrl.MyDiscordBot
import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class ShutdownCommand : CommandHandler(), CommandHandler.SlashCommandInterface{
    override fun execute(event: SlashCommandInteractionEvent) {
        if (event.user.id != MyDiscordBot.userID){
            event.reply("You don't have permission to do that!").setEphemeral(true).queue()
            return
        }

        event.reply("Zzzzzzzz...").setEphemeral(true)
            .queue { logger.info("Shutdown command issued by {}", event.user.name); event.jda.shutdown() }
    }

}
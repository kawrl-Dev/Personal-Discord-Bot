package dev.kawrl.botcommands

import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class PingCommand : CommandHandler(), CommandHandler.SlashCommandInterface {
    override fun execute(event: SlashCommandInteractionEvent) {
        val ping = event.jda.gatewayPing
        event.reply("Pong! 🏓 ($ping ms)").queue()
        logger.info("Bot has been pinged by {}",event.user.name)
    }
}
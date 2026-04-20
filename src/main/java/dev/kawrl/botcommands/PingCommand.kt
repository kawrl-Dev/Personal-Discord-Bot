package dev.kawrl.botcommands

import dev.kawrl.interfaces.SlashCommandInterface
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.slf4j.LoggerFactory

private val logger: org.slf4j.Logger = LoggerFactory.getLogger(PingCommand::class.java)

class PingCommand : SlashCommandInterface {
    override fun execute(event: SlashCommandInteractionEvent) {
        val ping = event.jda.gatewayPing
        event.reply("Pong! 🏓 ($ping ms)").queue()
        logger.info("Bot has been pinged by {}",event.user.name)
    }
}
package dev.kawrl.botcommands

import dev.kawrl.MyDiscordBot
import dev.kawrl.interfaces.SlashCommandInterface
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(ShutdownCommand::class.java)
class ShutdownCommand : SlashCommandInterface{
    override fun execute(event: SlashCommandInteractionEvent) {
        if (!event.user.id.equals(MyDiscordBot::userID)){
            event.reply("You don't have permission to do that!").setEphemeral(true).queue()
            return
        }

        event.reply("Zzzzzzzz...").setEphemeral(true)
            .queue { logger.info("Shutdown command issued by {}", event.user.name); event.jda.shutdown() }
    }
}
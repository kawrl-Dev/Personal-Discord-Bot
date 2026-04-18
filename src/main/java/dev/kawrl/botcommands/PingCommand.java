package dev.kawrl.botcommands;

import dev.kawrl.interfaces.SlashCommandInterface;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingCommand implements SlashCommandInterface {
    private static final Logger log = LoggerFactory.getLogger(PingCommand.class);

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long ping = event.getJDA().getGatewayPing();
        event.reply(String.format("Pong! 🏓 (%d ms)", ping)).queue();
        log.info("Bot has been pinged by {}", event.getUser().getName());
    }
}


package dev.kawrl.botcommands;

import dev.kawrl.MyDiscordBot;
import dev.kawrl.interfaces.SlashCommandInterface;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownCommand implements SlashCommandInterface {
    private static final Logger log = LoggerFactory.getLogger(ShutdownCommand.class);
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getUser().getId().equals(MyDiscordBot.userID)) {
            event.reply("You don't have permission to do that.").setEphemeral(true).queue();
            return;
        }
        event.reply("Zzzzzzzz...").setEphemeral(true).queue(_ -> {
            log.info("Shutdown command issued by {}", event.getUser().getName());
            event.getJDA().shutdown();
        });
    }
}

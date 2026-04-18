package dev.kawrl;

import dev.kawrl.botcommands.PingCommand;
import dev.kawrl.botcommands.ShutdownCommand;
import dev.kawrl.interfaces.SlashCommandInterface;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Listeners extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Listeners.class);
    private final Map<String, SlashCommandInterface> commands = new HashMap<>();

    public Listeners() {
        commands.put("ping", new PingCommand());
        commands.put("shutdown", new ShutdownCommand());
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInterface command = commands.get(event.getName().toLowerCase());
        if (command != null){
            try {
                command.execute(event);
            } catch (Exception e) {
                log.error("Error executing command '{}': {}", event.getName(), e.toString());
                event.reply("Something went wrong.").setEphemeral(true).queue();
            }
        }

        else log.warn("Unknown command received: {}", event.getName());
    }
}
package dev.kawrl.Utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static dev.kawrl.Utils.MyBotCommands.*;

public class Listeners extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName().toLowerCase()){
            case "ping":
                pingBotCommand(event);
                break;
            case "shutdown":
                MyBotCommands.shutdownCommand(event);
                break;
        }
    }
}
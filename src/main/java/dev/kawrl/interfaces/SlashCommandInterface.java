package dev.kawrl.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandInterface {
    void execute(SlashCommandInteractionEvent event);
}

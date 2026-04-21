package dev.kawrl.interfaces;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommandHandler {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    public interface SlashCommandInterface{
        void execute(SlashCommandInteractionEvent event);
    }

    public interface ModalInterface{
        void execute(ModalInteractionEvent event);
    }

    public interface StringSelectMenuInterface{
        void handle(StringSelectInteractionEvent event);
    }

    public interface ButtonInterface{
        void handle(ButtonInteractionEvent event);
    }
}

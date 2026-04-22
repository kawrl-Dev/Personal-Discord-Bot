package dev.kawrl.interfaces;

import dev.kawrl.database.TaskRepo;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

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

    protected void replyWithListSelector(SlashCommandInteractionEvent event, String menuID, String prompt) throws SQLException{
        String userId = Objects.requireNonNull(event.getMember()).getId();
        Map<String, Long> lists = TaskRepo.getListNamesForUser(userId);

        if (lists.isEmpty()){
            event.reply("You have no task lists yet! Create one first with '/create-list'")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create(menuID).setPlaceholder("Choose a List");
        lists.forEach((name,id) -> menuBuilder.addOption(name,id.toString()));

        event.reply(prompt)
                .addComponents(ActionRow.of(menuBuilder.build()))
                .setEphemeral(true)
                .queue();
    }
}

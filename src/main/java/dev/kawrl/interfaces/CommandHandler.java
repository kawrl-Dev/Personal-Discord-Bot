package dev.kawrl.interfaces;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
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

    protected void replyWithListSelector(SlashCommandInteractionEvent event, String menuID, String prompt, TaskRepositoryInterface repositoryInterface) throws SQLException{
        event.deferReply(true).queue();

        String userId = Objects.requireNonNull(event.getMember()).getId();
        Map<String, Long> lists = repositoryInterface.getListNamesForUser(userId);

        if (lists.isEmpty()){
            event.getHook().sendMessage("You have no task lists yet! Create one first with '/create-list'")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create(menuID).setPlaceholder("Choose a List");
        lists.forEach((name,id) -> menuBuilder.addOption(name,id.toString()));

        event.getHook().editOriginal(prompt)
                .setComponents(ActionRow.of(menuBuilder.build()))
                .queue();
    }

    @FunctionalInterface
    protected interface ReplyAction{
        ReplyCallbackAction reply(String message);
    }

    protected void replyWithConfirmation(
            ReplyAction replyAction,
            ButtonSpec confirm,
            ButtonSpec cancel,
            String prompt
    ) {
        Button yesButton = Button.success(confirm.getId(), confirm.getLabel());
        Button noButton  = Button.danger(cancel.getId(),  cancel.getLabel());

        replyAction.reply(prompt)
                .addComponents(ActionRow.of(yesButton, noButton))
                .setEphemeral(true)
                .queue();
    }
}

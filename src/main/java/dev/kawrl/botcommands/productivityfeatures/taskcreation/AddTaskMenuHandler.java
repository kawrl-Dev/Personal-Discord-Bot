package dev.kawrl.botcommands.productivityfeatures.taskcreation;

import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class AddTaskMenuHandler extends CommandHandler implements CommandHandler.StringSelectMenuInterface {
    @Override
    public void handle(StringSelectInteractionEvent event) {
        String listId = event.getValues().getFirst();
        event.replyModal(AddTaskModal.buildAddTaskModal(listId)).queue();
    }
}
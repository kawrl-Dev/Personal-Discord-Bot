package dev.kawrl.botcommands.productivityfeatures;

import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class CreateTaskModal implements CommandHandler.StringSelectMenuInterface {
    @Override
    public void handle(StringSelectInteractionEvent event) {
        String listId = event.getValues().getFirst();

        event.replyModal(TaskModalFactory.buildAddTaskModal(listId)).queue();
    }
}

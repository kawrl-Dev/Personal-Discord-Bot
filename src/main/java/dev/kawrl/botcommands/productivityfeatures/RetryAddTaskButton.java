package dev.kawrl.botcommands.productivityfeatures;

import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class RetryAddTaskButton extends CommandHandler implements CommandHandler.ButtonInterface {
    @Override
    public void handle(ButtonInteractionEvent event) {
        String listId = event.getComponentId().split(":")[1];

        event.replyModal(TaskModalFactory.buildAddTaskModal(listId)).queue();
    }
}
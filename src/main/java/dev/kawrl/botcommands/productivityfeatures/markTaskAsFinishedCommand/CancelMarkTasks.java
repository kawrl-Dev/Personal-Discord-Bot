package dev.kawrl.botcommands.productivityfeatures.markTaskAsFinishedCommand;

import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class CancelMarkTasks extends CommandHandler implements CommandHandler.ButtonInterface {
    @Override
    public void handle(ButtonInteractionEvent event) {
        Message message = event.getMessage();
        message.delete().queue();
        logger.info("Process of marking tasks as done cancelled by '{}'",event.getUser());
    }
}

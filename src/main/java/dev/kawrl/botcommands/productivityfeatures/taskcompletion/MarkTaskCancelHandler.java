package dev.kawrl.botcommands.productivityfeatures.taskcompletion;

import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class MarkTaskCancelHandler extends CommandHandler implements CommandHandler.ButtonInterface {
    @Override
    public void handle(ButtonInteractionEvent event) {
        logger.info("Process of marking tasks as done cancelled by '{}'", event.getUser().getName());
        event.reply("Cancelled.").setEphemeral(true).queue();
    }
}

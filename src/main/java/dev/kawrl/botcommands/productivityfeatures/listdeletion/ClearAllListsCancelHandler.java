package dev.kawrl.botcommands.productivityfeatures.listdeletion;

import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ClearAllListsCancelHandler extends CommandHandler implements CommandHandler.ButtonInterface {
    @Override
    public void handle(ButtonInteractionEvent event) {
        logger.info("Clear all lists cancelled by '{}'.", event.getUser().getName());
        event.reply("Cancelled! All your lists are safe.").setEphemeral(true).queue();
    }
}

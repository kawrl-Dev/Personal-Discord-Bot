package dev.kawrl.botcommands.productivityfeatures.listdeletion;

import dev.kawrl.database.TaskRepo;
import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.sql.SQLException;

public class ClearAllListsHandler extends CommandHandler implements CommandHandler.ButtonInterface {
    @Override
    public void handle(ButtonInteractionEvent event) {
        Member member = event.getMember();

        if (member != null){
            String userID = member.getId();
            String username = member.getUser().getName();

            try {
                int deletedCount = TaskRepo.clearAllListsForUser(userID);
                logger.info("Cleared {} list(s) for user '{}'",deletedCount,username);
                event.reply(String.format("✅ Done! Deleted **%d** list(s) and all their tasks.",deletedCount))
                        .setEphemeral(true).queue();
            } catch (SQLException e) {
                logger.error("DB error while clearing lists for user'{}': {}", username, e.toString());
                event.reply("A database error occured. Please try again.")
                        .setEphemeral(true).queue();
            }
        }
    }
}

package dev.kawrl.botcommands.productivityfeatures.markTaskAsFinishedCommand;

import dev.kawrl.database.TaskRepo;
import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HandleButtonPrompt extends CommandHandler implements CommandHandler.ButtonInterface {

    @Override
    public void handle(ButtonInteractionEvent event) {
        String[] parts = event.getComponentId().split(":",2);
        String[] taskIDs = parts[1].split(",");

        List<String> marked = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for(String rawID: taskIDs){
            long taskID;
            try{
                taskID = Long.parseLong(rawID);
            } catch (NumberFormatException e) {
                logger.warn("Invalid task ID in button component: '{}'", rawID);
                continue;
            }

            try {
                boolean success = TaskRepo.completeTask(taskID);
                if (success) marked.add("`#" + taskID + "`");
                else failed.add("`#" + taskID + "`");
            } catch (SQLException e) {
                logger.error("DB error marking task #{} as complete: {}", taskID, e.toString());
                failed.add("`#" + taskID + "`");
            }
        }

        StringBuilder reply = new StringBuilder();

        if (!marked.isEmpty()){
            reply.append(String.format("**%d task(s) marked as finished!**\n",marked.size()));
            reply.append("Tasks: ").append(String.join(", ",marked));
        }
        if (!failed.isEmpty() && !reply.isEmpty()){
            reply.append("\n")
                    .append("⚠️ Could not mark: ")
                    .append(String.join(", ",failed));
        }

        event.reply(reply.toString()).setEphemeral(true).queue();
    }
}

package dev.kawrl.botcommands.productivityfeatures;

import dev.kawrl.database.TaskRepo;
import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.sql.SQLException;

public class CreateNewTaskListCommand extends CommandHandler implements CommandHandler.SlashCommandInterface {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        OptionMapping option = event.getOption("list-name");

        if (option == null || member == null) return;

        String listName = option.getAsString();
        String username = member.getUser().getName();
        String userId   = member.getId();

        try {
            // Ensure the user row exists before inserting the list (FK constraint)
            TaskRepo.upsertUser(userId, username);

            if (TaskRepo.listExistsForUser(userId, listName)) {
                event.reply(String.format("You already have a task list named **%s**!", listName))
                        .setEphemeral(true)
                        .queue();
                logger.warn("User '{}' tried to create a duplicate task list: '{}'", username, listName);
                return;
            }

            long listId = TaskRepo.createTaskList(userId, listName);
            logger.info("New task list '{}' (id={}) created for user '{}'", listName, listId, username);
            event.reply(String.format("**New Task List Created!** Name: *%s*", listName))
                    .setEphemeral(true)
                    .queue();

        } catch (SQLException e) {
            logger.error("Database error while creating task list for user '{}': {}", username, e.toString());
            event.reply("A database error occurred. Please try again later.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
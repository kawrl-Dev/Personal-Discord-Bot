package dev.kawrl.botcommands.productivityfeatures;

import dev.kawrl.database.TaskRepo;
import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.SQLException;
import java.util.Map;

public class AddTaskCommand extends CommandHandler implements CommandHandler.SlashCommandInterface {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        if (member == null) return;

        // Process of Add Task Command below

        String username = member.getUser().getName();

        try {
            Map<String, Long> lists = TaskRepo.getListNamesForUser(member.getId());

            if (lists.isEmpty()) {
                event.reply("You have no task lists yet! Create one first with '/create-list'")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("select-list").setPlaceholder("Choose a Task List");
            lists.forEach((list_name,list_id) -> menuBuilder.addOption(list_name,String.valueOf(list_id)));

            event.reply("Which list would you like to add a task to?")
                    .addComponents(ActionRow.of((ActionRowChildComponent) menuBuilder))
                    .queue();


        } catch (SQLException e) {
            logger.error("Database error while getting task lists from user '{}': {}", username, e.toString());
            event.reply("A database error occurred. Please try again later.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
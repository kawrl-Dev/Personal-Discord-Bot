package dev.kawrl.botcommands.productivityfeatures;

import dev.kawrl.MyDiscordBot;
import dev.kawrl.botcommands.productivityfeatures.classes.TaskList;
import dev.kawrl.botcommands.productivityfeatures.classes.UserTaskData;
import dev.kawrl.interfaces.SlashCommandInterface;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;

public class CreateNewTaskListCommand implements SlashCommandInterface {
    private static final Logger log = LoggerFactory.getLogger(CreateNewTaskListCommand.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        OptionMapping option = event.getOption("list-name");

        if (option == null || member == null) return;

        String taskListName = option.getAsString();
        String username = member.getEffectiveName();
        String userID = member.getId();

        Path jsonDir = MyDiscordBot.resolveJsonFilesDirectory();
        Path userFile = jsonDir.resolve(username + ".json");

        UserTaskData userData;

        // Load existing file or create fresh UserTaskData
        if (userFile.toFile().exists()) {
            userData = mapper.readValue(userFile.toFile(), UserTaskData.class);
        } else {
            userData = new UserTaskData(username);
        }

        // Check for duplicate list name
        if (userData.hasListWithName(taskListName)) {
            event.reply(String.format("You already have a task list named **%s**!", taskListName))
                    .setEphemeral(true)
                    .queue();
            log.warn("User '{}' tried to create a duplicate task list: '{}'", username, taskListName);
            return;
        }

        // Append new list and save
        TaskList newList = new TaskList(userID, taskListName);
        userData.addTaskList(newList);
        mapper.writerWithDefaultPrettyPrinter().writeValue(userFile.toFile(), userData);

        log.info("New task list '{}' created for user '{}'", taskListName, username);
        event.reply(String.format("**New Task List Created!** Name: **%s**", taskListName))
                .setEphemeral(true)
                .queue();
    }
}
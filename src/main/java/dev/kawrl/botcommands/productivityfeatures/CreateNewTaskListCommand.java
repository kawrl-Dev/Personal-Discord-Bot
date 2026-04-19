package dev.kawrl.botcommands.productivityfeatures;

import dev.kawrl.botcommands.productivityfeatures.classes.TaskList;
import dev.kawrl.interfaces.SlashCommandInterface;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateNewTaskListCommand implements SlashCommandInterface {
    private static final Logger log = LoggerFactory.getLogger(CreateNewTaskListCommand.class);

    TaskList list;
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        OptionMapping option = event.getOption("list-name");
        if (option != null && member != null) {
            String taskListName = option.getAsString();

            list = new TaskList(member.getId(),taskListName);
            log.info("A new task list has been created! Task List Name: {}",list.getListName());

            // Response
            event.reply(String.format("**New Task List Created!** Name of List: %s",list.getListName()))
                    .setEphemeral(true)
                    .queue();
        }
    }
}

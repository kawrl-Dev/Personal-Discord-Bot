package dev.kawrl.botcommands.productivityfeatures;

import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

public class SelectTaskListMenu implements CommandHandler.StringSelectMenuInterface {
    @Override
    public void handle(StringSelectInteractionEvent event) {
        String componentId = event.getComponentId();

        if (!componentId.startsWith("select-list:")) return;

        // Scope check — only the user who invoked /add-task can use this menu
        String expectedUserId = componentId.split(":")[1];
        if (!event.getUser().getId().equals(expectedUserId)) {
            event.reply("This menu isn't for you!").setEphemeral(true).queue();
            return;
        }

        String listId = event.getValues().getFirst();

        TextInput taskInput = TextInput.create("task-text", TextInputStyle.SHORT)
                .setPlaceholder("Enter your task here...")
                .setRequired(true)
                .build();

        Modal modal = Modal.create("add-task:" + listId,"Create Task")
                .addComponents(
                        Label.of("Task",taskInput)
                )
                .build();

        event.replyModal(modal).queue();
    }
}

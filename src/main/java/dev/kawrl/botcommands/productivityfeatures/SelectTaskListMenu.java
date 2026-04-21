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
        String listId = event.getValues().getFirst();

        TextInput task_text = TextInput.create("task_string", TextInputStyle.SHORT)
                .setPlaceholder("e.g., Touching grass")
                .setMaxLength(512)
                .setRequired(true)
                .build();

        TextInput priorityLevel = TextInput.create("priority-level",TextInputStyle.SHORT)
                .setPlaceholder("LOW / MEDIUM / HIGH")
                .setMinLength(3)
                .setMaxLength(6)
                .setRequired(true)
                .build();

        TextInput taskStatus = TextInput.create("task-status",TextInputStyle.SHORT)
                .setPlaceholder("PENDING / FINISHED")
                .setMinLength(7)
                .setMaxLength(8)
                .setRequired(true)
                .build();

        TextInput deadline = TextInput.create("deadline",TextInputStyle.SHORT)
                .setPlaceholder("yyyy/MM/dd")
                .setMinLength(10)
                .setMaxLength(10)
                .setRequired(false)
                .build();

        Modal addTaskModal = Modal.create("add-task-modal: "+ listId,"Add Task to List")
                .addComponents(
                        Label.of("Task",task_text),
                        Label.of("Priority Level", priorityLevel),
                        Label.of("Task Status", taskStatus),
                        Label.of("Deadline",deadline)
                )
                .build();

        event.replyModal(addTaskModal).queue();
    }
}

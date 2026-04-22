package dev.kawrl.botcommands.productivityfeatures;

import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.modals.Modal;

public class TaskModalFactory {
    public static Modal buildAddTaskModal(String listId) {
        TextInput taskText = TextInput.create("task_string", TextInputStyle.SHORT)
                .setPlaceholder("e.g., Touching grass")
                .setMaxLength(512)
                .setRequired(true)
                .build();

        TextInput priorityLevel = TextInput.create("priority-level", TextInputStyle.SHORT)
                .setPlaceholder("LOW / MEDIUM / HIGH")
                .setMinLength(3)
                .setMaxLength(6)
                .setRequired(true)
                .build();

        TextInput deadline = TextInput.create("deadline", TextInputStyle.SHORT)
                .setPlaceholder("yyyy/MM/dd")
                .setMinLength(9)
                .setMaxLength(10)
                .setRequired(false)
                .build();

        return Modal.create("add-task-modal:" + listId, "Add Task to List")
                .addComponents(
                        Label.of("Task",taskText),
                        Label.of("Priority Level",priorityLevel),
                        Label.of("Deadline",deadline)
                )
                .build();
    }
}
package dev.kawrl.botcommands.productivityfeatures.tasksearch;

import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.modals.Modal;

public class SearchTaskModal {
    public static Modal build(){
        TextInput keyword = TextInput.create("search_keyword", TextInputStyle.SHORT)
                .setPlaceholder("e.g., Touching grass")
                .setMinLength(1)
                .setMaxLength(50)
                .setRequired(true)
                .build();

        return Modal.create("search-task-modal", "Search Tasks")
                .addComponents(Label.of("Keyword", keyword))
                .build();
    }
}

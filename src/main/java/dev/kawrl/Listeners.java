package dev.kawrl;

import dev.kawrl.botcommands.PingCommand;
import dev.kawrl.botcommands.ShutdownCommand;
import dev.kawrl.botcommands.productivityfeatures.AddTaskModalHandler;
import dev.kawrl.botcommands.productivityfeatures.CreateNewTaskListCommand;
import dev.kawrl.botcommands.productivityfeatures.SelectTaskListMenu;
import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class Listeners extends ListenerAdapter {
    private final Map<String, CommandHandler.SlashCommandInterface> slashCommands = new HashMap<>();
    private final Map<String, CommandHandler.ModalInterface> modalHandlers = new HashMap<>();
    private final Map<String, CommandHandler.StringSelectMenuInterface> menuSelectHandlers = new HashMap<>();

    public Listeners() {
        // Slash Commands
        slashCommands.put("ping", new PingCommand());
        slashCommands.put("shutdown", new ShutdownCommand());
        slashCommands.put("create-list", new CreateNewTaskListCommand());

        /*
        * Modal Interactions & String Select Menus
        * - None for now TODO[Update it if there are new modal/menu interactions]
        * */
        modalHandlers.put("add-task-modal:", new AddTaskModalHandler());

        // Menu Select
        menuSelectHandlers.put("select-list",new SelectTaskListMenu());
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        CommandHandler.SlashCommandInterface command = slashCommands.get(event.getName().toLowerCase());
        if (command != null) try {
            command.execute(event);
        } catch (Exception e) {
            event.reply("Something went wrong!")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String modalId = event.getModalId();
        CommandHandler.ModalInterface handler = modalHandlers.entrySet().stream()
                .filter(entry -> modalId.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
        if (handler != null) try {
            handler.execute(event);
        } catch (Exception e) {
            event.reply("Something went wrong!")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        CommandHandler.StringSelectMenuInterface handler = menuSelectHandlers.get(event.getComponentId());
        if (handler != null) try {
            handler.handle(event);
        } catch (Exception e) {
            event.reply("Something went wrong!")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
package dev.kawrl;

import dev.kawrl.botcommands.PingCommand;
import dev.kawrl.botcommands.ShutdownCommand;
import dev.kawrl.botcommands.productivityfeatures.listdeletion.ClearAllListsCancelHandler;
import dev.kawrl.botcommands.productivityfeatures.listdeletion.ClearAllListsCommand;
import dev.kawrl.botcommands.productivityfeatures.listdeletion.ClearAllListsHandler;
import dev.kawrl.botcommands.productivityfeatures.taskcreation.*;
import dev.kawrl.botcommands.productivityfeatures.taskcompletion.*;
import dev.kawrl.botcommands.productivityfeatures.taskdisplay.ViewListCommand;
import dev.kawrl.botcommands.productivityfeatures.taskdisplay.ViewListHandler;
import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Listeners extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Listeners.class);
    private final Map<String, CommandHandler.SlashCommandInterface> slashCommands = new HashMap<>();
    private final Map<String, CommandHandler.ModalInterface> modalHandlers = new HashMap<>();
    private final Map<String, CommandHandler.StringSelectMenuInterface> menuSelectHandlers = new HashMap<>();
    private final Map<String, CommandHandler.ButtonInterface> buttonHandlers = new HashMap<>();

    public Listeners() {
        // Slash Commands
        slashCommands.put("ping", new PingCommand());
        slashCommands.put("shutdown", new ShutdownCommand());
        slashCommands.put("create-list", new CreateListCommand());
        slashCommands.put("add-task", new AddTaskCommand());
        slashCommands.put("mark-task", new MarkTaskCommand());
        slashCommands.put("view-list", new ViewListCommand());
        slashCommands.put("clear-all-lists", new ClearAllListsCommand());

        // Menu Select Handlers
        menuSelectHandlers.put("select-list:add-task", new AddTaskMenuHandler());

        menuSelectHandlers.put("select-list:mark-task", new TaskSelectionHandler());
        menuSelectHandlers.put("select-list:view-list", new ViewListHandler());
        menuSelectHandlers.put("approve-selected-tasks:", new MarkTaskConfirmation());

        // Modal Handlers
        modalHandlers.put("add-task-modal:", new AddTaskSubmissionHandler());

        //Button Handlers
        buttonHandlers.put("retry-add-task:", new AddTaskRetryHandler());
        buttonHandlers.put("yes-response:", new MarkTaskHandler());
        buttonHandlers.put("no-response", new MarkTaskCancelHandler());
        buttonHandlers.put("confirm-clear-yes",new ClearAllListsHandler());
        buttonHandlers.put("confirm-clear-no",new ClearAllListsCancelHandler());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandPath = event.getSubcommandName() != null ? event.getSubcommandName(): event.getName();

        CommandHandler.SlashCommandInterface command = slashCommands.get(commandPath.toLowerCase());
        if (command != null) try {
            command.execute(event);
        } catch (Exception e) {
            event.reply("Something went wrong!")
                    .setEphemeral(true)
                    .queue();
            logger.warn("Error for Slash Command: {}", e.toString());
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
            logger.warn("Error for Modal Interaction: {}", e.toString());
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String menuID = event.getComponentId();
        CommandHandler.StringSelectMenuInterface handler = menuSelectHandlers.entrySet().stream()
                .filter(entry -> menuID.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
        if (handler != null) try {
            handler.handle(event);
        } catch (Exception e) {
            event.reply("Something went wrong!")
                    .setEphemeral(true)
                    .queue();
            logger.warn("Error for String Selection Menu: {}", e.toString());
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        CommandHandler.ButtonInterface handler = buttonHandlers.entrySet().stream()
                .filter(entry -> buttonId.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
        if (handler != null) try {
            handler.handle(event);
        } catch (Exception e) {
            event.reply("Something went wrong!")
                    .setEphemeral(true)
                    .queue();
            logger.warn("Error for Button Interaction: {}", e.toString());
        }
    }
}
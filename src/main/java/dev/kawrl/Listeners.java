package dev.kawrl;

import dev.kawrl.botcommands.PingCommand;
import dev.kawrl.botcommands.ShutdownCommand;
import dev.kawrl.botcommands.productivityfeatures.AddTaskCommand;
import dev.kawrl.botcommands.productivityfeatures.AddTaskModalHandler;
import dev.kawrl.botcommands.productivityfeatures.CreateNewTaskListCommand;
import dev.kawrl.botcommands.productivityfeatures.RetryAddTaskButton;
import dev.kawrl.botcommands.productivityfeatures.CreateTaskModal;
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
        slashCommands.put("create-list", new CreateNewTaskListCommand());
        slashCommands.put("add-task", new AddTaskCommand());

        // Modal Handlers
        modalHandlers.put("add-task-modal:", new AddTaskModalHandler());

        // Menu Select Handlers
        menuSelectHandlers.put("select-list", new CreateTaskModal());

        //Button Handlers
        buttonHandlers.put("retry-add-task:", new RetryAddTaskButton());
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
        CommandHandler.StringSelectMenuInterface handler = menuSelectHandlers.get(event.getComponentId());
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
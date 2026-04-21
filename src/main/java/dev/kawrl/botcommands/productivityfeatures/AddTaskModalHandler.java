package dev.kawrl.botcommands.productivityfeatures;

import dev.kawrl.database.TaskRepo;
import dev.kawrl.interfaces.CommandHandler;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class AddTaskModalHandler extends CommandHandler implements CommandHandler.ModalInterface {
    private static final Set<String> validPriorities = Set.of("LOW", "MEDIUM", "HIGH");
    private static final Set<String> validStatuses   = Set.of("PENDING", "FINISHED");
    @Override
    public void execute(ModalInteractionEvent event) {
        String list_ID = event.getModalId().split(":")[1];

        ModalMapping taskTextMapping = event.getValue("task_string"),
                priorityMapping = event.getValue("priority-level"),
                taskStatusMapping = event.getValue("task-status"),
                deadlineMapping = event.getValue("deadline");

        if ( taskTextMapping == null|| priorityMapping == null || taskStatusMapping == null) return;

        String taskString = taskTextMapping.getAsString(),
                priorityLVL = priorityMapping.getAsString().toUpperCase(),
                taskStatus = taskStatusMapping.getAsString().toUpperCase();

        if (!validPriorities.contains(priorityLVL)){
            replyWithRetry(event,list_ID,"Invalid priority! Please enter LOW, MEDIUM, or HIGH.");
            return;
        }

        if (!validStatuses.contains(taskStatus)){
            replyWithRetry(event,list_ID,"Invalid status! Please enter PENDING or FINISHED.");
            return;
        }

        Date deadline;

        if (deadlineMapping != null && !deadlineMapping.getAsString().isBlank()){
            try {
                LocalDate parsed = LocalDate.parse(
                        deadlineMapping.getAsString(),
                        DateTimeFormatter.ofPattern("yyyy/MM/dd")
                );
                deadline = java.sql.Date.valueOf(parsed);
            } catch (Exception e) {
                replyWithRetry(event,list_ID, "Invalid date format! Please use yyyy/MM/dd.");
                return;
            }

            try {
                long taskID = TaskRepo.addTask(Long.parseLong(list_ID),taskString,priorityLVL,deadline);
                logger.info("Task #{} added to the list #{} by {}",taskID,list_ID,event.getUser().getName());
                event.reply(String.format("Task added! (**%s** | %s | %s)",taskString,priorityLVL,taskStatus))
                        .setEphemeral(true)
                        .queue();
            } catch (Exception e) {
                logger.error("Database error while adding task for user '{}': {}", event.getUser().getName(), e.toString());
                event.reply("A database error occurred. Please try again later.")
                        .setEphemeral(true)
                        .queue();
            }
        }
    }

    private void replyWithRetry(ModalInteractionEvent event, String listID, String errorMsg){
        event.reply("❌ " + errorMsg)
                .addComponents(ActionRow.of(Button.primary("retry-add-task:"+ listID,"Try Again")))
                .setEphemeral(true)
                .queue();
    }
}

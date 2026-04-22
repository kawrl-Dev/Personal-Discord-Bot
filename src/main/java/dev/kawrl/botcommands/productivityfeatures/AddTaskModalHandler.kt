package dev.kawrl.botcommands.productivityfeatures

import dev.kawrl.database.TaskRepo
import dev.kawrl.interfaces.CommandHandler
import dev.kawrl.interfaces.CommandHandler.ModalInterface
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AddTaskModalHandler : CommandHandler(), ModalInterface {
    override fun execute(event: ModalInteractionEvent) {
        val listId =
            event.modalId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].trim { it <= ' ' }

        val taskTextMapping = event.getValue("task_string")
        val priorityMapping = event.getValue("priority-level")
        val deadlineMapping = event.getValue("deadline")

        if (taskTextMapping == null || priorityMapping == null) return

        val taskString = taskTextMapping.asString
        val priorityLVL = priorityMapping.asString.uppercase(Locale.getDefault())

        if (!validPriorities.contains(priorityLVL)) {
            replyWithRetry(event, listId, "Invalid priority! Please enter LOW, MEDIUM, or HIGH.")
            return
        }

        var deadline: Date? = null
        if (deadlineMapping != null && !deadlineMapping.asString.isBlank()) {
            try {
                val parsed = LocalDate.parse(
                    deadlineMapping.asString,
                    DateTimeFormatter.ofPattern("yyyy/M/d")
                )
                deadline = Date.valueOf(parsed)
            } catch (_: Exception) {
                replyWithRetry(event, listId, "Invalid date format! Please use yyyy/MM/dd.")
                return
            }
        }

        try {
            val taskID = TaskRepo.addTask(listId.toLong(), taskString, priorityLVL, deadline)
            logger.info("Task #{} added to list #{} by {}", taskID, listId, event.user.name)
            event.reply(String.format("Task added! (**%s** | %s)", taskString, priorityLVL))
                .setEphemeral(true)
                .queue()
        } catch (e: Exception) {
            logger.error("Database error while adding task for user '{}': {}", event.user.name, e.toString())
            event.reply("A database error occurred. Please try again later.")
                .setEphemeral(true)
                .queue()
        }
    }

    private fun replyWithRetry(event: ModalInteractionEvent, listID: String?, errorMsg: String?) {
        event.reply("❌ $errorMsg")
            .addComponents(ActionRow.of(Button.primary("retry-add-task:$listID", "Try Again")))
            .setEphemeral(true)
            .queue()
    }

    companion object {
        private val validPriorities = mutableSetOf<String?>("LOW", "MEDIUM", "HIGH")
    }
}
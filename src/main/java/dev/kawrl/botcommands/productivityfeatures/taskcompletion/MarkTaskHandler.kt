package dev.kawrl.botcommands.productivityfeatures.taskcompletion

import dev.kawrl.database.TaskRepo
import dev.kawrl.interfaces.CommandHandler
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import java.sql.SQLException

class MarkTaskHandler : CommandHandler(), CommandHandler.ButtonInterface {
    override fun handle(event: ButtonInteractionEvent) {
        // componentId format: "yes-response:<listID>|<taskID1>,<taskID2>,..."
        val afterPrefix = event.componentId.removePrefix("yes-response:")
        val pipeIndex = afterPrefix.indexOf('|')
        val listID = afterPrefix.substring(0, pipeIndex).toLong()
        val taskIDsRaw = afterPrefix.substring(pipeIndex + 1)

        val taskIDs = taskIDsRaw.split(",").filter { it.isNotBlank() }

        val marked = ArrayList<Long>()
        val failed = ArrayList<Long>()

        for (rawID in taskIDs) {
            val taskID: Long
            try {
                taskID = rawID.toLong()
            } catch (_: NumberFormatException) {
                logger.warn("Invalid task ID in button component: '{}'", rawID)
                continue
            }

            try {
                val success = TaskRepo.completeTask(taskID, listID)
                if (success) marked.add(taskID) else failed.add(taskID)
            } catch (e: SQLException) {
                logger.error("DB error marking task #{} as complete: {}", taskID, e.toString())
                failed.add(taskID)
            }
        }

        val reply = StringBuilder()
        if (marked.isNotEmpty()) {
            reply.append("**${marked.size} task(s) marked as finished!**\n")
            reply.append("Tasks: ").append(marked.joinToString(", "))
        }
        if (failed.isNotEmpty()) {
            if (reply.isNotEmpty()) reply.append("\n")
            reply.append("⚠️ Could not mark: ").append(failed.joinToString(", "))
        }

        event.reply(reply.toString()).setEphemeral(true).queue()
    }
}
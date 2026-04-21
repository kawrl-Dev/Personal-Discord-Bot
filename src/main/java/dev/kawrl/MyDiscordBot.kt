package dev.kawrl

// Dotenv
import io.github.cdimascio.dotenv.Dotenv

// Database
import dev.kawrl.database.DatabaseManager

// Java Discord API
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent

// Logger
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val dotenv: Dotenv = Dotenv.load()
private val log: Logger = LoggerFactory.getLogger(MyDiscordBot::class.java)

object MyDiscordBot {
    val userID: String = requireEnv("USER_ID")
    private val discordBotToken: String = requireEnv("BOT_API")

    @JvmStatic
    fun main(args: Array<String>) {
        DatabaseManager.initialize(dotenv)

        try {
            val registersCommands = args.isNotEmpty() && args[0] == "--register"

            val jda: JDA = JDABuilder.createDefault(discordBotToken)
                .enableIntents(
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.DIRECT_MESSAGES
                )
                .setActivity(Activity.customStatus("Status: Active!"))
                .addEventListeners(Listeners()).build()
                .awaitReady()

            if (registersCommands) {
                jda.updateCommands().addCommands(
                    Commands.slash("ping", "Checks if bot is alive"),
                    Commands.slash("shutdown", "Shuts down the Discord Bot."),
                    Commands.slash("create-list", "Creates a new task list")
                        .addOption(OptionType.STRING, "list-name", "Name of the task list", true),
                    Commands.slash("add-task","Create a task")
                ).queue()
                log.info("Commands registered!")
            }

            log.info("Bot started successfully.")

            Runtime.getRuntime().addShutdownHook(Thread {
                log.info("Shutdown hook triggered — closing DB pool.")
                DatabaseManager.shutdown()
            })

        } catch (e: Exception) {
            log.error("Something went wrong: {}", e.toString())
            DatabaseManager.shutdown()
        }
    }
}

private fun requireEnv(key: String): String {
    val value = dotenv[key]
    check(!value.isNullOrBlank()) { "Missing required environment variable: $key" }
    return value
}
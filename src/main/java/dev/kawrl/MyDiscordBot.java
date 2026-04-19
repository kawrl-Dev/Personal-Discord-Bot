package dev.kawrl;

// Dotenv
import io.github.cdimascio.dotenv.Dotenv;

// Java Discord API
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

// Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Java Libraries
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class MyDiscordBot {
    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger log = LoggerFactory.getLogger(MyDiscordBot.class);

    private static final String discordBotToken = requireEnv("BOT_API");
    public static final String userID = requireEnv("USER_ID");

    public static void main(String[] args) {

        Path jsonDir = resolveJsonFilesDirectory();

        try {
            Files.createDirectories(jsonDir);
            log.info("jsonFiles directory ready at: {}", jsonDir.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to create jsonFiles directory: {}", e.toString());
        }

        try {
            boolean registersCommands = args.length > 0 && args[0].equals("--register");
            JDA jda = JDABuilder.createDefault(discordBotToken)
                    .enableIntents(
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.DIRECT_MESSAGES
                    )
                    .setActivity(Activity.customStatus("Status: Active!"))
                    .addEventListeners(new Listeners()).build().awaitReady();

            if (registersCommands){
                jda.updateCommands().addCommands(
                        Commands.slash("ping","Checks if bot is alive"),
                        Commands.slash("shutdown","Shuts down the Discord Bot."),
                        Commands.slash("new-task-list","Creates new list for tasks")
                                .addOption(OptionType.STRING,"list-name","Name of Task List",true)
                ).queue();
                log.info("Commands Registered!");
            }

            log.info("Bot Started Successfully");
        } catch (Exception e) {
            log.error("Something went wrong: {}", e.toString());
        }
    }

    private static String requireEnv(String key){
        String value = MyDiscordBot.dotenv.get(key);
        if (value == null ||value.isBlank()){
            throw new IllegalStateException("Missing required environment variable: " + key);
        }
        return value;
    }

    public static Path resolveJsonFilesDirectory(){
        boolean isRunningFromJAR = Objects.requireNonNull(MyDiscordBot.class
                        .getResource("MyDiscordBot.class")).toString().startsWith("jar:");

        if (isRunningFromJAR) return Path.of("jsonFiles");
        else return Path.of("src","main","resources","jsonFiles");
    }
}
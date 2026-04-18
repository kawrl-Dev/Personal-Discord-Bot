package src.kawrlDev;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyDiscordBot {
    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger log = LoggerFactory.getLogger(MyDiscordBot.class);
    private static final String discordBotToken = dotenv.get("BOT_API");
    public static void main() {
        try {
            JDA jda = JDABuilder.createDefault(discordBotToken)
                    .setActivity(Activity.customStatus("Running in the background!"))
                    .addEventListeners(new Listeners())
                    .build()
                    .awaitReady();

            jda.updateCommands().addCommands(
                    Commands.slash("shutdown","Shuts down the Discord Bot.")
            ).queue();

            log.info("Bot Started Successfully");
        } catch (Exception e) {
            log.error("Something went wrong: {}", e.toString());
        }
    }
}
package src.kawrlDev;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class MyDiscordBot {
    private static final Dotenv dotenv = Dotenv.load();
    public static JDA jda;
    private static final String discordBotToken = dotenv.get("BOT_API");
    public static void main() {
        try {
            jda = JDABuilder.createDefault(discordBotToken)
                    .setActivity(Activity.customStatus("Running in the background!"))
                    .build();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }
}
package src.kawrlDev;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listeners extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Listeners.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName().toLowerCase()){
            case "ping":
                MyBotCommands.pingBotCommand(event);
                break;
            case "shutdown":
                MyBotCommands.shutdownCommand(event);
                break;
        }
    }

    private static class MyBotCommands{
        private static void pingBotCommand(SlashCommandInteractionEvent event){
            event.reply("Pong!").queue();
            log.info("Bot has been pinged by {}", event.getUser().getName());
        }

        private static void shutdownCommand(SlashCommandInteractionEvent event){
            if (!event.getUser().getId().equals(MyDiscordBot.userID)){
                event.reply("You don't have permission to do that.").setEphemeral(true).queue();
                return;

            }
            event.reply("Shutting down... goodbye!").setEphemeral(true).queue(_ -> {
                log.info("Shutdown command issued by {}", event.getUser().getName());
                event.getJDA().shutdown();
            });
        }
    }
}

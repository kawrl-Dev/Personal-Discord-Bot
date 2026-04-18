package dev.kawrl.Utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.kawrl.MyDiscordBot;

class MyBotCommands {
    private static final Logger log = LoggerFactory.getLogger(MyBotCommands.class);
    static void pingBotCommand(SlashCommandInteractionEvent event){
        long ping = event.getJDA().getGatewayPing();

        event.reply(String.format("Pong! 🏓 (%d ms)",ping)).queue();
        log.info("Bot has been pinged by {}", event.getUser().getName());
    }

    static void shutdownCommand(SlashCommandInteractionEvent event){
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
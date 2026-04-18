package src.kawrlDev;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listeners extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(Listeners.class);
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String content = message.getContentRaw();

        if (content.equals("!ping")){
            MessageChannel channel = event.getGuildChannel();
            channel.sendMessage("Pong!").queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("shutdown")) {

            if (!event.getUser().getId().equals("418022453030289420")) {
                event.reply("You don't have permission to do that.").setEphemeral(true).queue();
                return;
            }

            event.reply("Shutting down... goodbye!").queue(_ -> {
                log.info("Shutdown command issued by {}", event.getUser().getName());
                event.getJDA().shutdown();
            });
        }
    }
}

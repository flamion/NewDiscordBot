import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

interface Command {
    Mono<Void> execute(MessageCreateEvent event);
}

public class BotMain {

    private static final Map<String, Command> commands = new HashMap<>();


//    private final static DiscordClient client = DiscordClient.create(System.getenv("TOKEN"));
//    private final static GatewayDiscordClient gateway = client.login().block();
    private final static long UPSINCE = System.currentTimeMillis();
    final static String PREFIX = "-";


    static {
        commands.put("ping", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage("Pong!"))
                .then());
    }

    public static void main(String[] args) {
        final DiscordClient client = DiscordClient.create(System.getenv("TOKEN"));
        final GatewayDiscordClient gateway = client.login().block();

        gateway.getEventDispatcher().on(MessageCreateEvent.class)
                // 3.1 Message.getContent() is a String
                .flatMap(event -> Mono.just(event.getMessage().getContent())
                        .flatMap(content -> Flux.fromIterable(commands.entrySet())
                                // We will be using ! as our "prefix" to any command in the system.
                                .filter(entry -> content.startsWith(PREFIX + entry.getKey()))
                                .flatMap(entry -> entry.getValue().execute(event))
                                .next()))
                .subscribe();

        gateway.onDisconnect().block();
    }

}

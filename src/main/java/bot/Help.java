package bot;

public class Help {

    public static String getHelp(String input) {
        String[] stringArray = input.split(" ");
        return getHelpMessage(stringArray[1]);
    }

    public static String getHelpMessage(String input) {
        switch (input) {
            case "ping":
                return input + " \npong";
            case "join":
                return input + " lets the bot enter the channel from the executing user.";
            case "play":
                return input + " plays a song using a given URL. \nExample" + BotMain.PREFIX + input + " h t t p s : / / w w w . y o u t u b e . c o m / w a t c h ? v = m _ q l g F Q s 7 E 4";
            case "skip":
                return input + " will skip the currently playing song.";
            case "loop":
                return input + " will loop the currently playing song.";
            case "disconnect":
            case "fuckoff":
                return input + " will disconnect the bot if currently in voice channel.";
            case "botinfo":
                return input + " displays a few details about the bot.";
            case "help":
                return "...";
            default:
                return "Invalid input";
        }
    }
}

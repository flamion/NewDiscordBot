package bot;

public class Help {

    public static String getHelp(String input) {
        String[] stringArray = input.split(" ");
        return getHelpMessage(stringArray.length > 1 ? stringArray[1] : "");
    }

    public static String getHelpMessage(String input) {
        switch (input) {
            case "ping":
                return input + " \npong";
            case "join":
                return input + " lets the bot enter the channel from the executing user.";
            case "play":
                return input + " plays a song using a given URL. \nExample" + BotMain.PREFIX + input + "<https://www.youtube.com/watch?v=m_qlgFQs7E4>";
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

package bot;

public class TimeFormatter {
    //all get rounded down

    public static String secondFormatter(long millis) {
        return "" + millis / 1000;
    }

    public static String minuteFormatter(long millis) {
        return "" + millis / 60000;
    }

    public static String hourFormatter(long millis) {
        return "" + millis / 360000;
    }

    public static String generalTimeFormatter(long millis) {
        StringBuilder builder = new StringBuilder();
        long timeLeftHoursTotal = millis / 3600000;
        long timeLeftMinutesTotal = millis / 60000;
        long timeLeftSecondsTotal = millis / 1000;
        if (timeLeftHoursTotal < 10) {
            builder.append("0").append(timeLeftHoursTotal);
        } else {
            builder.append(" ").append(timeLeftHoursTotal);
        }
        if (timeLeftMinutesTotal % 60 < 10) {
            builder.append(":0").append(timeLeftMinutesTotal % 60);
        } else {
            builder.append(":").append(timeLeftMinutesTotal % 60);
        }
        if (timeLeftSecondsTotal % 60 < 10) {
            builder.append(":0").append(timeLeftSecondsTotal % 60).append("\n");
        } else {
            builder.append(":").append(timeLeftSecondsTotal % 60).append("\n");
        }
        return builder.toString();
    }

    public static String generalTimeFormatter(String preText, long millis) {
        StringBuilder builder = new StringBuilder();
        long timeLeftHoursTotal = millis / 3600000;
        long timeLeftMinutesTotal = millis / 60000;
        long timeLeftSecondsTotal = millis / 1000;
        if (timeLeftHoursTotal < 10) {
            builder.append(preText).append("0").append(timeLeftHoursTotal);
        } else {
            builder.append(preText).append(timeLeftHoursTotal);
        }
        if (timeLeftMinutesTotal % 60 < 10) {
            builder.append(":0").append(timeLeftMinutesTotal % 60);
        } else {
            builder.append(":").append(timeLeftMinutesTotal % 60);
        }
        if (timeLeftSecondsTotal % 60 < 10) {
            builder.append(":0").append(timeLeftSecondsTotal % 60).append("\n");
        } else {
            builder.append(":").append(timeLeftSecondsTotal % 60).append("\n");
        }
        return builder.toString();
    }

    /**
     * @param messageContent this is for the seek command. It takes the message content as input and converts the argument in the form hh:mm:ss to milliseconds.
     *                       It ignores faults (e.g. 32:12:as or 65:32:50)
     * @return returns the time converted to milliseconds or -1 if it was completely invalid
     *
     * Infos: This works only while hours < 60 and wont work for anything larger than hh:mm:ss (For example dd:hh:mm:ss, it will attempt to convert the days with incorrect count)
     */
    public static long seekToMillis(String messageContent) {
        String[] split = messageContent.split(" |:");

        if (split.length == 2 && split[1].matches("0|00")) {
            return 0L; //return 0 if content is 0, no need to do the rest
        }

        int count = 1;
        long millis = -1;
        for (int i = split.length - 1; i > 0; i--) {
            if (split[i].matches("[0-9]+") && Integer.parseInt(split[i]) < 59 && Integer.parseInt(split[i]) > 0) {
                millis += (Integer.parseInt(split[i]) * count * 1000);
            }
            count *= 60;
        }

        return millis < 0 ? -1 : millis + 1L;
    }
}

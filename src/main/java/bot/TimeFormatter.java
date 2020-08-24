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
}

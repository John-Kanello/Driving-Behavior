package com.example.drivingbehaviour.HelperClasses;

public class ConvertSecondsToTime {

    public String convertSecondsToTime(int time) {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        int numOfSeconds = time;

        while (numOfSeconds > 0) {
            if (numOfSeconds >= 3600) {
                hours++;
                numOfSeconds -= 3600;
            } else if (numOfSeconds >= 60) {
                minutes++;
                numOfSeconds -= 60;
            } else {
                seconds += numOfSeconds;
                numOfSeconds = 0;
            }
        }

        String h;
        String m;
        String s;

        if (hours < 10) {
            h = 0 + "" + hours;
        } else {
            h = String.valueOf(hours);
        }

        if (minutes < 10) {
            m = 0 + "" + minutes;
        } else {
            m = String.valueOf(minutes);
        }

        if (seconds < 10) {
            s = 0 + "" + seconds;
        } else {
            s = String.valueOf(seconds);
        }

        return h + ":" + m + ":" + s;
    }
}

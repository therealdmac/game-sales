package com.dmac.game_sales.utils;

import java.time.Duration;

public class DateTimeUtils {
    public static String toText(Duration duration){
        return duration.toHours()+"hrs "
                + duration.toMinutesPart() + "mins "
                + duration.toSecondsPart() + "."
                + duration.toMillisPart() + "secs";
    }
}

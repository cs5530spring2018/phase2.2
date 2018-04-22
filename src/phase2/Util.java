package phase2;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Util {
    public static float convertTime(String hour, String minute) {
        float floatHour, floatMinute;
        try{
            floatHour = Float.parseFloat(hour);
            floatMinute = Float.parseFloat(minute);
        } catch (Exception e) {
            System.err.println("Error converting time to float");
            return -1f;
        }
        return round(floatHour + (floatMinute/60f), 2);
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    public static Float getNowTimeAsFloat() {
        LocalDateTime now = LocalDateTime.now();
        return convertTime(Integer.toString(now.getHour()), Integer.toString(now.getMinute()));
    }
    /**Our DB entries have dates 0 (Sun) - 6 (Sat)
     * LocalDateTime has dates 1 (Mon) - 7 (Sun)
     * This adjusts the date so Sun uses 0 instead of 7*/
    public static int dayOfTheWeekAdjuster(int dayOfTheWeek){
        if(dayOfTheWeek == 7) { return 0; }
        return dayOfTheWeek;
    }
}

package phase2;

import java.math.BigDecimal;

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
}

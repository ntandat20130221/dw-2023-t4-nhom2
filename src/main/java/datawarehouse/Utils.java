package datawarehouse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Utils {
    public static boolean isToday(long date) {
        Calendar today = Calendar.getInstance();
        Calendar specifiedDate = Calendar.getInstance();
        specifiedDate.setTimeInMillis(date);
        return today.get(Calendar.DAY_OF_MONTH) == specifiedDate.get(Calendar.DAY_OF_MONTH)
                && today.get(Calendar.MONTH) == specifiedDate.get(Calendar.MONTH)
                && today.get(Calendar.YEAR) == specifiedDate.get(Calendar.YEAR);
    }

    public static int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String getPrizeName(String prize) {
        return switch (prize) {
            case "ĐB" -> "Giải đặc biệt";
            case "1" -> "Giải nhất";
            case "2" -> "Giải nhì";
            case "3" -> "Giải ba";
            case "4" -> "Giải tư";
            case "5" -> "Giải năm";
            case "6" -> "Giải sáu";
            case "7" -> "Giải bảy";
            case "8" -> "Giải tám";
            default -> null;
        };
    }

    public static String getDayOfWeek(String dateFormat, String date) {
        return switch (LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat)).getDayOfWeek().getValue()) {
            case 1 -> "Thứ 2";
            case 2 -> "Thứ 3";
            case 3 -> "Thứ 4";
            case 4 -> "Thứ 5";
            case 5 -> "Thứ 6";
            case 6 -> "Thứ 7";
            default -> "Chủ nhật";
        };
    }
}

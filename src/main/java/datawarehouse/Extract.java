package datawarehouse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Extract {
    public static final String BASE_URL = "https://xoso.com.vn/";
    public static final String DATE = "01-12-2023";

    public static void main(String[] args) throws Exception {
        Document document = Jsoup
                .connect(BASE_URL + "kqxs-" + DATE + ".html")
                .userAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
                .get();

        PrintWriter pw = new PrintWriter(getFileName());
        String date = DATE.replace('-', '/');
        String dow = getDayOfWeek();

        var tableResult = document.getElementsByClass("table-result");
        for (int i = 0; i < 3; i++) {
            var table = tableResult.get(i);
            if (i == 0) {
                var rows = table.select("tbody tr:not(:first-child)");
                for (var row : rows) {
                    String prizeName = getPrizeName(row.select("td:first-child").text());
                    String prizes = row.select("td:last-child").text();
                    if (prizes.contains(" ")) prizes = String.format("\"%s\"", prizes);
                    pw.println(String.join(",", date, dow, "Miền Bắc", "", prizeName, prizes));
                }
            } else {
                var provinces = table.select("thead th:not(:first-child)");
                for (int j = 0; j < provinces.size(); j++) {
                    var rows = table.select("tbody tr");
                    for (int k = rows.size() - 1; k >= 0; k--) {
                        var row = rows.get(k);
                        String prizeName = getPrizeName(row.selectFirst("th").text());
                        String prizes = row.select("td").get(j).text();
                        if (prizes.contains(" ")) prizes = String.format("\"%s\"", prizes);
                        pw.println(String.join(",", date, dow, i == 1 ? "Miền Nam" : "Miền Trung", provinces.get(j).text(), prizeName, prizes));
                    }
                }
            }
        }

        pw.close();
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

    public static String getFileName() {
        String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss"));
        return formattedDateTime + ".csv";
    }

    public static String getDayOfWeek() {
        return switch (LocalDate.parse(DATE, DateTimeFormatter.ofPattern("dd-MM-yyyy")).getDayOfWeek().getValue()) {
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

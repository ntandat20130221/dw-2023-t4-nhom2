package datawarehouse.extract;

import datawarehouse.Utils;
import datawarehouse.models.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Objects;

public class CrawlerImpl implements Crawler {

    @Override
    public String crawl(Config config, String date) {
        try {
            Document document = Jsoup
                    .connect(config.sourceUrl + date + config.sourceSuffix)
                    .userAgent(config.userAgent)
                    .get();

            StringBuilder sb = new StringBuilder();

            String dow = Utils.getDayOfWeek(config.dateFormat, date);
            var tableResult = document.getElementsByClass("table-result");
            for (int i = 0; i < 3; i++) {
                var table = tableResult.get(i);
                if (i == 0) {
                    var rows = table.select("tbody tr:not(:first-child)");
                    for (var row : rows) {
                        String prizeName = Utils.getPrizeName(row.select("td:first-child").text());
                        String prizes = row.select("td:last-child").text();
                        if (prizes.contains(" ")) prizes = String.format("\"%s\"", prizes);
                        sb.append(String.join(config.fileDelimiter, date, dow, "Miền Bắc", "", prizeName, prizes)).append("\n");
                    }
                } else {
                    var provinces = table.select("thead th:not(:first-child)");
                    for (int j = 0; j < provinces.size(); j++) {
                        var rows = table.select("tbody tr");
                        for (int k = rows.size() - 1; k >= 0; k--) {
                            var row = rows.get(k);
                            String prizeName = Utils.getPrizeName(Objects.requireNonNull(row.selectFirst("th")).text());
                            String prizes = row.select("td").get(j).text();
                            if (prizes.contains(" ")) prizes = String.format("\"%s\"", prizes);
                            sb.append(String.join(config.fileDelimiter, date, dow, i == 1 ? "Miền Nam" : "Miền Trung",
                                    provinces.get(j).text(), prizeName, prizes)).append("\n");
                        }
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}

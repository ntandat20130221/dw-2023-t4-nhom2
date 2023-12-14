package datawarehouse.extract;

import datawarehouse.models.Config;

public interface Crawler {
    String crawl(Config config, String date);
}

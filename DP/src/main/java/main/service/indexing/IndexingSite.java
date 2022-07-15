package main.service.indexing;

import lombok.Getter;
import lombok.Setter;
import main.dto.listSite.ListSites;
import main.repository.SiteRepository;
import main.service.insertToDB.InsertToDB;
import org.apache.logging.log4j.*;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class IndexingSite {
    @Setter
    @Getter
    private boolean isParsing = false;
    private final ListSites listSites;
    private final SiteRepository siteRepository;
    private final InsertToDB insertToDB;

    public IndexingSite(ListSites listSites,
                        SiteRepository siteRepository,
                        InsertToDB insertToDB) {
        this.listSites = listSites;
        this.siteRepository = siteRepository;
        this.insertToDB = insertToDB;
    }

    public void allSiteIndexing() {
        for (ListSites.SiteData s : listSites.getList()) {
            oneSiteIndexing(s.getUrl());
        }
    }

    public void oneSiteIndexing(String url) {
        if (isParsing()) {
            SiteParsing siteParsing = new SiteParsing(url);
            new ForkJoinPool().invoke(new HTMLParses(isParsing(), insertToDB, siteParsing));
        }
    }

    public void addOrUpdatePage(String url) {
        if (siteIsExist(url)) {
            oneSiteIndexing(url);
        }
    }

    public boolean siteIsExist(String url) {
        boolean result = false;
        Matcher matcher = Pattern.compile("(^https?://.+.(ru|com))").matcher(url);
        if (matcher.find()) {
            result = siteRepository.findSiteByUrl(matcher.group(1)) != null;
        }
        return result;
    }
}
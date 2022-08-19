package main.service.statistic;

import main.dto.statistics.Detailed;
import main.dto.statistics.InfoStatics;
import main.dto.statistics.Statistics;
import main.dto.statistics.Total;
import main.entity.StatusType;
import main.entity.Lemma;
import main.entity.Page;
import main.entity.Site;
import main.repository.*;
import main.service.indexing.IndexingSite;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MyStatisticImpl implements MyStatistic {

    private final PageRepository pageRepository;

    private final SiteRepository siteRepository;

    private final LemmaRepository lemmaRepository;

    public MyStatisticImpl(PageRepository pageRepository,
                           SiteRepository siteRepository,
                           LemmaRepository lemmaRepository) {
        this.pageRepository = pageRepository;
        this.siteRepository = siteRepository;
        this.lemmaRepository = lemmaRepository;
    }

    @Override
    public List<Site> getSite() {
        return siteRepository.findAll();
    }

    @Override
    public List<Page> getPage() {
        return pageRepository.findAll();
    }

    @Override
    public List<Lemma> getLemma() {
        return lemmaRepository.findAll();
    }


    @Override
    public InfoStatics getStatistic() {
        Total total = getTotal();
        List<Site> siteList = getSite();
        List<Detailed> list = new ArrayList<>();
        for (Site s : siteList) {
            Detailed detailed = getDetailed(s);
            list.add(detailed);
        }
        Statistics statisticClass = new Statistics(total, list);
        return new InfoStatics(true, statisticClass);
    }

    private Total getTotal() {
        long siteCount = getSite().size();
        long pageCount = getPage().size();
        long lemmaCount = getLemma().size();
        boolean isIndexing = isSitesIndexing();
        return new Total(siteCount, pageCount, lemmaCount, isIndexing);
    }

    private Detailed getDetailed(Site site) {
        int idSite = site.getId();
        String nameSite = site.getName();
        String urlSite = site.getUrl();
        String errorSite = site.getLastError();
        Timestamp timestamp = Timestamp.valueOf(site.getStatusTime());
        long timeSite = timestamp.getTime();
        StatusType statusSite = site.getStatus();
        int countSitePages = pageRepository.findCountPathOfId(idSite);
        int countSiteLemma = lemmaRepository.findCountLemmaOfId(idSite);
        return new Detailed(urlSite,
                nameSite,
                statusSite,
                timeSite,
                errorSite,
                countSitePages,
                countSiteLemma);
    }

    private boolean isSitesIndexing() {
        return getSite().stream()
                .anyMatch(s -> s.getStatus().equals(StatusType.INDEXING));
    }
}
package main.service.statistic;

import main.dto.statistics.InfoStatics;
import main.entity.Lemma;
import main.entity.Page;
import main.entity.Site;

import java.util.List;

public interface MyStatistic {
    List<Site> getSite();
    List<Page> getPage();
    List<Lemma> getLemma();
    InfoStatics getStatistic();
}
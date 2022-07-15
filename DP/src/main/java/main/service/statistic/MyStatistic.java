package main.service.statistic;

import main.dto.statistics.InfoStatics;
import main.entity.Lemma;
import main.entity.Page;
import main.entity.Site;

import java.util.List;

public interface MyStatistic {
    public List<Site> getSite();
    public List<Page> getPage();
    public List<Lemma> getLemma();
    public InfoStatics getStatistic();
}
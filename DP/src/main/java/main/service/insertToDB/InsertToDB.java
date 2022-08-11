package main.service.insertToDB;

import main.entity.*;
import main.repository.*;
import main.service.morphology.LuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InsertToDB {
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final FieldRepository fieldRepository;
    private final IndexTRepository indexTRepository;

    public InsertToDB(PageRepository pageRepository,
                      SiteRepository siteRepository,
                      LemmaRepository lemmaRepository,
                      FieldRepository fieldRepository,
                      IndexTRepository indexTRepository) {
        this.pageRepository = pageRepository;
        this.siteRepository = siteRepository;
        this.lemmaRepository = lemmaRepository;
        this.fieldRepository = fieldRepository;
        this.indexTRepository = indexTRepository;
    }

    @Autowired
    public void insertField() {
        if (!fieldRepository.existsById(1)) {
            Field title = new Field("title", "title", 1.0f);
            Field body = new Field("body", "body", 0.8f);
            List<Field> fieldList = List.of(title, body);
            fieldRepository.saveAllAndFlush(fieldList);
        }
    }

    public void existSite(String url, String name) {
        if (!siteRepository.existsSiteByUrl(url)) {
            Site site = new Site();
            site.setStatus(StatusType.INDEXED);
            site.setStatusTime(LocalDateTime.now());
            site.setLastError("ошибок нет");
            site.setUrl(url);
            site.setName(name);
            siteRepository.saveAndFlush(site);
        }
    }

    public void addToPage(String path, int statusCode, String content, String url) {
        Matcher matcher = Pattern.compile("(https*://[\\da-zA-Zа-яА-Я.-]+)").matcher(url);
        if (matcher.find()) {
            Site site = siteRepository.findSiteByUrl(matcher.group());
            Page page = new Page();
            page.setPath(path);
            page.setCode(statusCode);
            page.setContent(content);
            page.setSite(site);
            pageRepository.save(page);
            if (statusCode == 200) creatingLemmas(page, site.getId());
        }
    }

    public void creatingLemmas(Page page, int siteId) {
        LuceneMorphology luceneMorphology = new LuceneMorphology();
        String content = page.getContent();

        Document document = Jsoup.parse(content);
        String title = document.title();
        String body = document.body().text();

        List<String> listTitle = luceneMorphology.luceneToList(title);
        Map<String, Long> lemTitle = luceneMorphology.luceneToMap(listTitle);
        listTitle.forEach(s -> addOrUpdateLemma(s, siteId));

        List<String> listBody = luceneMorphology.luceneToList(body);
        Map<String, Long> lemBody = luceneMorphology.luceneToMap(listBody);
        listBody.forEach(s -> addOrUpdateLemma(s, siteId));

        addIndexGeneral(page, lemTitle, lemBody);
    }

    private void addOrUpdateLemma(String lemma, int siteId) {
        int frequency = 1;
        Lemma l = lemmaRepository.findLemmaByLemma(lemma);
        if (l == null) {
            addLemma(lemma, frequency, siteId);
        } else {
            updateLemma(lemma, l.getFrequency() + 1);
        }
    }

    private void addLemma(String lemma, int frequency, int siteId) {
        Site site = siteRepository.findSiteById(siteId);
        Lemma lemma1 = new Lemma();
        lemma1.setLemma(lemma);
        lemma1.setFrequency(frequency);
        lemma1.setSite(site);
        lemmaRepository.save(lemma1);
    }

    private void updateLemma(String lemma, int frequency) {
        Lemma lemmaFirst = lemmaRepository.findLemmaByLemma(lemma);
        Lemma lemma1 = new Lemma();
        lemma1.setId(lemmaFirst.getId());
        lemma1.setLemma(lemma);
        lemma1.setFrequency(frequency);
        lemma1.setSite(lemmaFirst.getSite());
        lemmaRepository.save(lemma1);
    }

    public void addIndexGeneral(Page page, Map<String, Long> lemTitle, Map<String, Long> lemBody) {
        List<Lemma> all = lemmaRepository.findAll();
        float titleFloat = fieldRepository.findWeightByName("title");
        float bodyFloat = fieldRepository.findWeightByName("body");
        for (Lemma l : all) {
            String lem = l.getLemma();
            float rank = 0;

            for (Map.Entry<String, Long> set : lemTitle.entrySet()) {
                if (lem.equals(set.getKey())) rank += titleFloat * set.getValue();
            }

            for (Map.Entry<String, Long> set : lemBody.entrySet()) {
                if (lem.equals(set.getKey())) rank += bodyFloat * set.getValue();
            }

            if (rank != 0) {
                addToIndex(page, l, rank);
            }
        }
    }

    private void addToIndex(Page page, Lemma lemma, float rank) {
        Index index = new Index(page, lemma, rank);
        indexTRepository.save(index);
    }
}
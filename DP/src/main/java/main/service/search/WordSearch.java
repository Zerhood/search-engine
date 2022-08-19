package main.service.search;

import main.dto.search.Data;
import main.entity.Lemma;
import main.entity.Page;
import main.entity.Site;
import main.repository.LemmaRepository;
import main.repository.PageRepository;
import main.repository.SiteRepository;
import main.service.morphology.LuceneMorphology;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WordSearch {
    private final LemmaRepository lemmaRepository;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private static final String REGEX_TITLE = "(<title>)(.+)(</title>)";

    private TreeSet<Lemma> inputStringToLemma = new TreeSet<>(Comparator.comparingInt(Lemma::getFrequency));
    private List<Page> listPageByLemmas = new ArrayList<>();

    public WordSearch(LemmaRepository lemmaRepository,
                      PageRepository pageRepository,
                      SiteRepository siteRepository) {
        this.lemmaRepository = lemmaRepository;
        this.pageRepository = pageRepository;
        this.siteRepository = siteRepository;
    }

    public void transformInputStrToLemmas(String input) {
        LuceneMorphology luceneMorphology = new LuceneMorphology();
        List<String> listInput = luceneMorphology.luceneToList(input);
        for (String s : listInput) {
            Lemma lemma = lemmaRepository.findLemmaByLemma(s);
            if (lemma != null) inputStringToLemma.add(lemma);
        }
        if (!inputStringToLemma.isEmpty()) {
            listPageByLemmas = pageRepository.getAllPageByLemma(inputStringToLemma.first().getLemma());
            if (listPageByLemmas.size() != 0) {
                inputStringToLemma.forEach(this::createPagesList);
            }
        }
    }

    private void createPagesList(Lemma lemma) {
        List<Page> innerList = pageRepository.getAllPageByLemma(lemma.getLemma());
        listPageByLemmas.retainAll(innerList);
    }

    public List<Data> printingListOfPages(String site) {
        if (listPageByLemmas.size() != 0) {
            List<Integer> pageId = new ArrayList<>();
            List<Integer> lemmaId = new ArrayList<>();

            for (Page p : listPageByLemmas) {
                pageId.add(p.getId());
            }
            for (Lemma l : inputStringToLemma) {
                lemmaId.add(l.getId());
            }

            List<Float> sumRank =
                    pageRepository.getSumRank(pageId, lemmaId);
            Float maxRank = Collections.max(sumRank);
            List<Object[]> pathAndContentAndRelevance =
                    pageRepository.getPathAndContentAndRelevance(maxRank, pageId, lemmaId);

            List<Data> list = new ArrayList<>();
            for (Object[] o : pathAndContentAndRelevance) {
                String path = (String) o[0];
                String content = (String) o[1];
                double relevance = (double) o[2];

                StringBuilder builder = new StringBuilder();
                for (Lemma l : inputStringToLemma) {
                    builder.append(createSnippet(content, l.getLemma())).append(System.lineSeparator());
                }

                //-----------------------------------------------------------------------
                if (site != null) {
                    Site siteByUrl = siteRepository.findSiteByUrl(site);
                    Data dataResult = createDataResult(siteByUrl.getUrl(),
                            siteByUrl.getName(),
                            path,
                            getTitle(content),
                            builder.toString(),
                            relevance);

                    list.add(dataResult);
                } else {
                    List<Site> siteList = siteRepository.findAll();
                    for (Site s : siteList) {
                        Site siteByUrl = siteRepository.findSiteByUrl(s.getUrl());
                        Data dataResult = createDataResult(siteByUrl.getUrl(),
                                siteByUrl.getName(),
                                path,
                                getTitle(content),
                                builder.toString(),
                                relevance);

                        list.add(dataResult);
                    }
                }
                //-------------------------------------------------------------------------
            }
            inputStringToLemma.clear();
            listPageByLemmas.clear();
            return list;
        } else {
            return null;
        }
    }

    private String createSnippet(String input, String lemma) {
        StringBuilder outPut = new StringBuilder();
        String REGEX_LEMMA = "(.{0,20}" + lemma + ".{0,20})";
        Matcher mat = Pattern.compile(REGEX_LEMMA).matcher(input);
        while (mat.find()) {
            String regex = "(.+)(" + lemma + ")(.+)";
            outPut.append(mat.group().replaceAll(regex, "$1"))
                    .append("<b>")
                    .append(mat.group().replaceAll(regex, "$2"))
                    .append("</b>")
                    .append(mat.group().replaceAll(regex, "$3"))
                    .append("<br>");
        }
        return outPut.toString();
    }

    private String getTitle(String content) {
        Matcher matcher = Pattern.compile(REGEX_TITLE).matcher(content);
        return (matcher.find()) ? matcher.group(2) : "";
    }

    private Data createDataResult(String url,
                                  String name,
                                  String path,
                                  String title,
                                  String snippet,
                                  double relevance) {
        Data data = new Data();
        data.setSite(url);
        data.setSiteName(name);
        data.setUri(path);
        data.setTitle(title);
        data.setSnippet(snippet);
        data.setRelevance(relevance);
        return data;
    }
}
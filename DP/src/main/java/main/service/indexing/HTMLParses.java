package main.service.indexing;

import main.service.insertToDB.InsertToDB;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLParses extends RecursiveAction {
    private final InsertToDB insertToDB;
    private final SiteParsing parsing;
    private boolean isIndexing;
    public static final String REGEX = "(https*://[\\da-zA-Zа-яА-Я.-]+)";
    private static final String REGEX_FOR_CONTENT = "'";

    public HTMLParses(boolean isIndexing,
                      InsertToDB insertToDB,
                      SiteParsing parsing) {
        this.isIndexing = isIndexing;
        this.insertToDB = insertToDB;
        this.parsing = parsing;
    }

    @Override
    protected void compute() {
        if (!isIndexing) {
            return;
        }
        Set<HTMLParses> set = new HashSet<>();
        try {
            Thread.sleep(500);
            Connection.Response response = Jsoup.connect(parsing.getUrl())
                    .method(Connection.Method.GET)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("https://www.google.com")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .execute();
            Document document = response.parse();
            String content = document.html();
            int sitemapStatus = response.statusCode();
            String title = document.title();

            Matcher matcher = Pattern.compile(REGEX).matcher(parsing.getUrl());
            if (matcher.find()) {
                insertToDB.existSite(matcher.group(), title);
            }

            insertToDB.addToPage(replaceUrl(parsing.getUrl()), sitemapStatus, replaceContent(content), parsing.getUrl());

            Elements elements = document.select("body").select("a");
            for (Element a : elements) {
                String site = a.absUrl("href");
                if (isCorrectUrl(site)) {
                    site = stripParams(site);
                    parsing.addChild(new SiteParsing(site));
                }
            }
            for (SiteParsing p : parsing.getChildren()) {
                HTMLParses htmlParses = new HTMLParses(isIndexing, insertToDB, p);
                set.add(htmlParses);
                htmlParses.fork();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        for (HTMLParses p : set) {
            p.join();
        }
    }

    private String replaceUrl(String url) {
        return url.replaceAll(REGEX, "");
    }

    private String replaceContent(String content) {
        return content.replaceAll(REGEX_FOR_CONTENT, "`");
    }

    private String stripParams(String url) {
        return url.replaceAll("\\?.+", "");
    }

    private boolean isCorrectUrl(String url) {
        Pattern patternRoot = Pattern.compile("^" + parsing.getUrl());
        Pattern patternNotFile = Pattern.compile("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)");
        Pattern patternNotAnchor = Pattern.compile("#([\\w\\-]+)?$");

        return patternRoot.matcher(url).lookingAt()
                && !patternNotFile.matcher(url).find()
                && !patternNotAnchor.matcher(url).find();
    }
}
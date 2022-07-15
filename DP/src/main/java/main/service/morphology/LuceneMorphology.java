package main.service.morphology;

import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LuceneMorphology {
    private static final String REGEX_PART_OF_THE_LANGUAGE = "(МЕЖД)|(СОЮЗ)|(ПРЕДЛ)|(ЧАСТ)";
    private static final String REGEX_NOT_CYRILLIC = "[^а-яА-ЯёЁ]+";
    private static final String REGEX_CYRILLIC = "[а-яА-ЯёЁ]+";
    private org.apache.lucene.morphology.LuceneMorphology luceneMorphology;

    public List<String> luceneToList(String text) {
        try {
            luceneMorphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Stream.of(text)
                .map(s -> s.replaceAll(REGEX_NOT_CYRILLIC, "\s"))
                .map(s -> s.split("\s+"))
                .flatMap(Arrays::stream)
                .filter(this::isCyrillic)
                .map(s -> s.trim().toLowerCase(Locale.ROOT))
                .parallel()
                .map(s -> luceneMorphology.getNormalForms(s))
                .flatMap(Collection::stream)
                .filter(this::isOfficialPartOfSpeech)
                .toList();
    }

    public Map<String, Long> luceneToMap(List<String> list) {
        return list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private boolean isCyrillic(String str) {
        return (str != null) && (str.length() != 1) && (!str.equals("")) && (str.matches(REGEX_CYRILLIC));
    }

    private boolean isOfficialPartOfSpeech(String word) {
        return luceneMorphology.getMorphInfo(word)
                .stream()
                .anyMatch(s -> {
                    Matcher matcher = Pattern.compile(REGEX_PART_OF_THE_LANGUAGE).matcher(s);
                    return !matcher.find();
                });
    }
}
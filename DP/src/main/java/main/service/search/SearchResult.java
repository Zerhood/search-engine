package main.service.search;

import main.dto.search.Data;
import main.dto.search.SearchDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SearchResult {
    private final WordSearch wordSearch;

    public SearchResult(WordSearch wordSearch) {
        this.wordSearch = wordSearch;
    }

    public SearchDTO getSearchResult(String query, String site, int offSet, int limit) {
        wordSearch.transformInputStrToLemmas(query);
        List<Data> list = wordSearch.printingListOfPages(site);
        if (list != null) {
            int count = (limit + offSet < list.size()) ? limit : list.size() - offSet;
            list = list.subList(0, count);
            return new SearchDTO(true, count, list);
        } else {
            list = new ArrayList<>();
            int count = (limit + offSet < 0) ? limit : -offSet;
            list = list.subList(0, count);
            return new SearchDTO(true, count, list);
        }
    }
}
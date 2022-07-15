package main.controller;

import main.dto.resultDTO.ResultDTO;
import main.service.indexing.IndexingSite;
import main.service.search.SearchResult;
import main.service.statistic.MyStatisticImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MyController {

    private final MyStatisticImpl myStatistic;
    private final IndexingSite indexingSite;
    private final SearchResult searchResult;

    public MyController(MyStatisticImpl myStatistic,
                        IndexingSite indexingSite,
                        SearchResult searchResult) {
        this.myStatistic = myStatistic;
        this.indexingSite = indexingSite;
        this.searchResult = searchResult;
    }

    @GetMapping("/")
    public String showSite() {
        return "index";
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<?> startIndexing() {
        if (!indexingSite.isParsing()) {
            indexingSite.setParsing(true);
            indexingSite.allSiteIndexing();
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok().body(new ResultDTO("Индексация не запущена"));
        }
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<?> stopIndexing() {
        if (indexingSite.isParsing()) {
            indexingSite.setParsing(false);
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok().body(new ResultDTO("Индексация не запущена"));
        }
    }

    @PostMapping("/indexPage")
    public ResponseEntity<?> indexPage(@RequestParam String url) {
        if (indexingSite.siteIsExist(url)) {
            indexingSite.addOrUpdatePage(url);
            return ResponseEntity.ok().body(new ResultDTO());
        } else {
            return ResponseEntity
                    .ok()
                    .body(new ResultDTO("Данная страница находится за пределами сайтов, " +
                            "указанных в конфигурационном файле"));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> statistics() {
        return ResponseEntity.ok().body(myStatistic.getStatistic());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String query,
                                    @RequestParam(required = false) String site,
                                    @RequestParam(required = false, defaultValue = "0") int offSet,
                                    @RequestParam(required = false, defaultValue = "20") int limit) {
        if (query.isEmpty()) {
            return ResponseEntity.ok().body(new ResultDTO("Задан пустой поисковый запрос"));
        } else {
            return ResponseEntity.ok().body(searchResult.getSearchResult(query, site, offSet, limit));
        }
    }
}
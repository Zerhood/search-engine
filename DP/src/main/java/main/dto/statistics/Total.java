package main.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Total {
    @JsonProperty("sites")
    private long sites;
    @JsonProperty("pages")
    private long pages;
    @JsonProperty("lemmas")
    private long lemmas;
    @JsonProperty("isIndexing")
    private boolean isIndexing;
}
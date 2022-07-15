package main.dto.statistics;

import lombok.*;
import main.entity.StatusType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Detailed {
    private String url;
    private String name;
    private StatusType status;
    private long statusTime;
    private String error;
    private long pages;
    private long lemmas;
}
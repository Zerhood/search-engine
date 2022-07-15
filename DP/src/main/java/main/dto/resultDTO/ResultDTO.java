package main.dto.resultDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultDTO {
    @JsonProperty("result")
    private boolean result;
    @JsonProperty("error")
    private String error;

    public ResultDTO() {
        this.result = true;
    }

    public ResultDTO(String error) {
        this.result = false;
        this.error = error;
    }
}
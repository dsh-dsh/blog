package main.dto.responses;

import lombok.Data;

@Data
public class ResultResponse {
    private boolean result;

    public ResultResponse() {
        this.result = true;
    }
}

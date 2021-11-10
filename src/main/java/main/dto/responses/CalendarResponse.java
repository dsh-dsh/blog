package main.dto.responses;

import lombok.Data;
import java.util.Map;

@Data
public class CalendarResponse {
    private int[] years;
    private Map<String, Long> posts;

    public CalendarResponse(int[] years, Map<String, Long> posts) {
        this.years = years;
        this.posts = posts;
    }
}

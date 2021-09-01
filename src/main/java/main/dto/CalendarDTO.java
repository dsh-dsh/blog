package main.dto;

import lombok.Getter;

import java.util.Date;

@Getter
public class CalendarDTO {

    private final String date;
    private final long count;

    public CalendarDTO(Date date, long count) {
        this.date = date.toString();
        this.count = count;
    }
}

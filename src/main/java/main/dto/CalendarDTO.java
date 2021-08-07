package main.dto;

import lombok.Getter;

import java.util.Date;

@Getter
public class CalendarDTO {

    private String date;
    private long count;

    public CalendarDTO(Date date, long count) {
        this.date = date.toString();
        this.count = count;
    }
}

package main.dto.requests;

import lombok.Data;
import main.Constants;

import javax.validation.constraints.Size;

@Data
public class PostRequest {

    private int id;

    private long timestamp;

    private boolean active;

    @Size(min = 5, message = Constants.TITLE_TOO_SHORT)
    private String title;

    private String[] tags;

    @Size(min = 50, message = Constants.TEXT_TOO_SHORT)
    private String text;

}

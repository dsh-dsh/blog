package main.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDTO {

    private int id;
    private long timestamp;
    private String text;
    private UserDTO user;

}

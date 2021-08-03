package main.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Tag;

import java.util.List;

@Data
@NoArgsConstructor
public class PostDTOSingle {

    private int id;
    private long timestamp;
    private UserDTO user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private List<CommentDTO> comments;
    private List<Tag> tags;
}

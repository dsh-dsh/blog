package main.dto.responses;

import lombok.Data;
import main.dto.PostDTOSingle;

@Data
public class PostResponseSingle {

    private PostDTOSingle post;
    private String[] tags;

    public PostResponseSingle(PostDTOSingle post, String[] tags) {
        this.post = post;
        this.tags = tags;
    }
}

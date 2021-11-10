package main.dto.responses;

import lombok.Data;
import main.dto.PostDTO;
import java.util.List;

@Data
public class PostResponse {

    private long count;
    private List<PostDTO> posts;

    public PostResponse(long count, List<PostDTO> posts) {
        this.count = count;
        this.posts = posts;
    }
}

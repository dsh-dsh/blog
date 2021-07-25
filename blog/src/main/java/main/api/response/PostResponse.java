package main.api.response;

import lombok.Data;
import main.dto.PostDTO;

import java.util.List;

@Data
public class PostResponse {

    private int count;
    private List<PostDTO> posts;

    public PostResponse(int count, List<PostDTO> postDTOList) {
        this.count = count;
        this.posts = postDTOList;
    }
}

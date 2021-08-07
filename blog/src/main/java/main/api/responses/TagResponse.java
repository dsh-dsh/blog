package main.api.responses;

import lombok.Data;
import main.dto.TagDTO;
import java.util.List;

@Data
public class TagResponse {
    private List<TagDTO> tags;

    public TagResponse(List<TagDTO> tags) {
        this.tags = tags;
    }
}

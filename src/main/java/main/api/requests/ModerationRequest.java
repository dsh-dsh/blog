package main.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.model.ModerationStatus;

@Data
public class ModerationRequest {

    @JsonProperty("post_id")
    private int id;

    private ModerationStatus decision;
}

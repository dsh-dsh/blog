package main.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ModerationStatus {
    @JsonProperty("new")
    NEW,
    @JsonProperty("accepted")
    ACCEPTED,
    @JsonProperty("declined")
    DECLINED
}

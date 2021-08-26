package main.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum ModerationStatus {
    @JsonProperty("new")
    NEW,
    @JsonAlias({"accepted", "accept"})
    ACCEPTED,
    @JsonAlias({"declined", "decline"})
    DECLINED
}

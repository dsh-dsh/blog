package main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Integer id;
    private String name;
    private String photo;
    private String email;
    @JsonProperty("moderation")
    private boolean isModerator;
    private int moderationCount;
    private boolean settings;
}

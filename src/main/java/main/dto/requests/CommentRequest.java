package main.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.Constants;
import main.validation.anotations.IsParentExists;
import main.validation.anotations.IsPostExists;

import javax.validation.constraints.Size;

@Data
public class CommentRequest {

    @JsonProperty("parent_id")
    @IsParentExists
    private int parentId;

    @JsonProperty("post_id")
    @IsPostExists
    private int postId;

    @Size(min = 20, message = Constants.TEXT_TOO_SHORT)
    private String text;

}
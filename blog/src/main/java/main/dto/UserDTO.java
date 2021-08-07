package main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.User;

@Data
@NoArgsConstructor
public class UserDTO {

    private Integer id;
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String photo;
}

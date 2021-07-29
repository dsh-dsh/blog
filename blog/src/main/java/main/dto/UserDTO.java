package main.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.User;

@Data
@NoArgsConstructor
public class UserDTO {

    private Integer id;
    private String name;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}

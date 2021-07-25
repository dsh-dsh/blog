package main.dto;

import lombok.Data;
import main.model.User;

import javax.persistence.Column;
import java.util.Date;

@Data
public class UserDTO {

    private Integer id;
    private String name;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}

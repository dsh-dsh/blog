package main.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.dto.UserDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private boolean result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDTO user;
}

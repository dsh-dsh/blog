package main.api.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.Constants;
import main.validation.OnCreate;
import main.validation.OnUpdate;
import main.validation.anotations.Captcha;
import main.validation.anotations.IsEmailExists;

import javax.validation.constraints.Email;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Captcha
public class UserRequest {

    @JsonAlias({"e_mail", "email"})
    @Email
    @IsEmailExists
    private String email;

    @Size(min = 6, message = Constants.PASSWORD_TOO_SHORT)
    private String password;

    @Pattern(regexp = "[\\w\\s@]+", message = Constants.WRONG_NAME)
    private String name;

    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;

    private boolean removePhoto;

}

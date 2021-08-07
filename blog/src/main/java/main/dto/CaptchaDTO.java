package main.dto;

import lombok.Data;
import lombok.Getter;

@Getter
public class CaptchaDTO {
    private String secret;
    private String image;

    public CaptchaDTO(String secret, String image) {
        this.secret = secret;
        this.image = image;
    }
}

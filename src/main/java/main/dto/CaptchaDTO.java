package main.dto;

import lombok.Getter;

@Getter
public class CaptchaDTO {
    private final String secret;
    private final String image;

    public CaptchaDTO(String secret, String image) {
        this.secret = secret;
        this.image = image;
    }
}

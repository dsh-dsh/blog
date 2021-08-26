package main.validation.anotations;

import main.Constants;
import main.validation.OnCreate;
import main.validation.validators.CaptchaValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CaptchaValidator.class)
public @interface Captcha {

    String message() default Constants.WRONG_CAPTCHA_CODE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

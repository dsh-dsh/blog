package main.validation.anotations;

import main.Constants;
import main.validation.validators.EmailExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailExistsValidator.class)
public @interface IsEmailExists {

    String message() default Constants.EMAIL_EXISTS;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}


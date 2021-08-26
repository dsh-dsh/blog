package main.validation.anotations;

import main.Constants;
import main.validation.validators.CodeExpiredValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodeExpiredValidator.class)
public @interface IsCodeExpired {

    String message() default Constants.CODE_RESTORE_EXPIRED;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

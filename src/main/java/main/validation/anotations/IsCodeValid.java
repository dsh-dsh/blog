package main.validation.anotations;

import main.Constants;
import main.validation.validators.CodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CodeValidator.class)
public @interface IsCodeValid {

    String message() default Constants.WRONG_CODE_RESTORE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

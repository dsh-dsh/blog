package main.validation.validators;

import main.validation.anotations.IsCodeExpired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CodeExpiredValidator implements ConstraintValidator<IsCodeExpired, String> {

    @Value("${restore.code.expired.hours}")
    private int hoursToExpired;

    @Override
    public boolean isValid(String code, ConstraintValidatorContext constraintValidatorContext) {

        if(code == null) return true;

        try {
            long currentHours = System.currentTimeMillis()/(1000*60*60);
            long hours = Long.parseLong(code.substring(code.lastIndexOf("*") + 1));
            return (currentHours - hours) < hoursToExpired;

        } catch(NumberFormatException ex) {
            return false;
        }
    }
}

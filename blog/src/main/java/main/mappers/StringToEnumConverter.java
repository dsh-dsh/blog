package main.mappers;

import main.model.ModerationStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, ModerationStatus> {
    @Override
    public ModerationStatus convert(String source) {
        return ModerationStatus.valueOf(source.toUpperCase());
    }
}

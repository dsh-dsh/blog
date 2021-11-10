package main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.Constants;

@Data
public class SettingsDTO {

    @JsonProperty(Constants.MULTIUSER_MODE)
    private boolean multiuserMode;

    @JsonProperty(Constants.POST_PREMODERATION)
    private boolean postPremoderation;

    @JsonProperty(Constants.STATISTICS_IS_PUBLIC)
    private boolean statisticIsPublic;

}

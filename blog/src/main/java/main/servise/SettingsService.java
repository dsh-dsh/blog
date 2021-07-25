package main.servise;

import main.api.response.SettingsResponse;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    public SettingsResponse getGlobalSettings() {
        SettingsResponse settingsResponse = new SettingsResponse();
        settingsResponse.setMultiuserMode(true);
        settingsResponse.setPostPremoderation(true);
        settingsResponse.setStatisticIsPublic(true);
        return settingsResponse;
    }

}

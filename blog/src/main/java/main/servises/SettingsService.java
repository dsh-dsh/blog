package main.servises;

import main.api.responses.SettingsResponse;
import main.model.GlobalSetting;
import main.repositories.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    public SettingsResponse getGlobalSettings() {

        SettingsResponse settingsResponse = new SettingsResponse();

        List<GlobalSetting> settingList = settingsRepository.findAll();
        for(GlobalSetting setting : settingList) {
            switch (setting.getCode()) {
                case "MULTIUSER_MODE":
                    settingsResponse.setMultiuserMode(setting.getValue().equals("YES"));
                    break;
                case "POST_PREMODERATION":
                    settingsResponse.setPostPremoderation(setting.getValue().equals("YES"));
                    break;
                case "STATISTICS_IS_PUBLIC":
                    settingsResponse.setStatisticIsPublic(setting.getValue().equals("YES"));
            }
        }

        return settingsResponse;
    }

}

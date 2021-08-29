package main.servises;

import main.dto.SettingsDTO;
import main.model.GlobalSetting;
import main.repositories.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.function.Function;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    public SettingsDTO getGlobalSettings() {

        SettingsDTO settingsDTO = new SettingsDTO();

        List<GlobalSetting> settingList = settingsRepository.findAll();
        for(GlobalSetting setting : settingList) {
            switch (setting.getCode()) {
                case "MULTIUSER_MODE":
                    settingsDTO.setMultiuserMode(setting.getValue().equals("YES"));
                    break;
                case "POST_PREMODERATION":
                    settingsDTO.setPostPremoderation(setting.getValue().equals("YES"));
                    break;
                case "STATISTICS_IS_PUBLIC":
                    settingsDTO.setStatisticIsPublic(setting.getValue().equals("YES"));
            }
        }

        return settingsDTO;
    }

    public void setGlobalSettings(SettingsDTO settings) {
        settingsRepository.updateSetting(convertSettingCode(settings.isMultiuserMode()), "MULTIUSER_MODE");
        settingsRepository.updateSetting(convertSettingCode(settings.isPostPremoderation()), "POST_PREMODERATION");
        settingsRepository.updateSetting(convertSettingCode(settings.isStatisticIsPublic()), "STATISTICS_IS_PUBLIC");
    }

    public String convertSettingCode(boolean value) {
        return value ? "YES" : "NO";
    }


}

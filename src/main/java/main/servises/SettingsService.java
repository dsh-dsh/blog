package main.servises;

import main.Constants;
import main.dto.SettingsDTO;
import main.model.GlobalSetting;
import main.repositories.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    public SettingsDTO getGlobalSettings() {

        SettingsDTO settingsDTO = new SettingsDTO();

        List<GlobalSetting> settingList = settingsRepository.findAll();
        for(GlobalSetting setting : settingList) {
            switch (setting.getCode()) {
                case Constants.MULTIUSER_MODE:
                    settingsDTO.setMultiuserMode(setting.getValue().equals(Constants.YES));
                    break;
                case Constants.POST_PREMODERATION:
                    settingsDTO.setPostPremoderation(setting.getValue().equals(Constants.YES));
                    break;
                case Constants.STATISTICS_IS_PUBLIC:
                    settingsDTO.setStatisticIsPublic(setting.getValue().equals(Constants.YES));
            }
        }

        return settingsDTO;
    }

    public void setGlobalSettings(SettingsDTO settings) {
        settingsRepository.updateSetting(convertSettingCode(settings.isMultiuserMode()), Constants.MULTIUSER_MODE);
        settingsRepository.updateSetting(convertSettingCode(settings.isPostPremoderation()), Constants.POST_PREMODERATION);
        settingsRepository.updateSetting(convertSettingCode(settings.isStatisticIsPublic()), Constants.STATISTICS_IS_PUBLIC);
    }

    public String convertSettingCode(boolean value) {
        return value ? Constants.YES : Constants.NO;
    }


}

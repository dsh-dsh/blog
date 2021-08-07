package main.repositories;

import main.model.GlobalSetting;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SettingsRepository extends CrudRepository<GlobalSetting, Integer> {
    List<GlobalSetting> findAll();
}

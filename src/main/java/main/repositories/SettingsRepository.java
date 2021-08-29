package main.repositories;

import main.model.GlobalSetting;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SettingsRepository extends CrudRepository<GlobalSetting, Integer> {
    List<GlobalSetting> findAll();

    @Modifying
    @Transactional
    @Query("UPDATE GlobalSetting gs SET gs.value = :value WHERE gs.code = :code")
    void updateSetting(String value, String code);
}

package main.repositories;

import main.model.Captcha;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Date;

public interface CaptchaRepository  extends CrudRepository<Captcha, Integer> {

    @Modifying
    @Transactional
    void deleteByTimeLessThan(Date date);

    boolean existsByCodeAndSecretCode(String code, String secretCode);
}

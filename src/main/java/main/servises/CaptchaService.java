package main.servises;

import com.github.cage.Cage;
import com.github.cage.GCage;
import main.dto.CaptchaDTO;
import main.model.CaptchaCode;
import main.repositories.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class CaptchaService {

    @Autowired
    private CaptchaRepository captchaRepository;
    @Value("${time.expired.mc}")
    private String timeExpired;

    public CaptchaDTO getNewCaptcha() {

        String code = UUID.randomUUID().toString().substring(0, 6);
        String secretCode = UUID.randomUUID().toString().replaceAll("-", "");
        String base64Image = getBase64Image(code);

        CaptchaCode captchaCode = new CaptchaCode(new Date(), code, secretCode);
        captchaRepository.save(captchaCode);
        deleteOldCaptcha();

        return new CaptchaDTO(secretCode, base64Image);

    }

    public boolean isCaptchaValid(String code, String secretCode) {

        return captchaRepository.existsByCodeAndSecretCode(code, secretCode);

    }

    public void deleteOldCaptcha() throws NumberFormatException{

        Date dateExpired = new Date();
        dateExpired.setTime(new Date().getTime() - Long.parseLong(timeExpired));
        captchaRepository.deleteByTimeLessThan(dateExpired);

    }

    public String getBase64Image(String code) {

        byte[] byteArray = makeCaptchaByteArray(code);
        String encodedString = Base64.getEncoder().encodeToString(byteArray);
        return "data:image/png;base64, " + encodedString;

    }

    public byte[] makeCaptchaByteArray(String text) {

//        // при отправке размера 100*35 как в задании каптча становится менее читаемой
//        // тем более, что на фронтенде ее преобразуют в размер 85*30
//        Painter painter = new Painter(100, 35, null, null, null, null);
//        Cage cage = new Cage(painter, null, null, null, null, null, null);
        Cage cage = new GCage();
        return cage.draw(text);

    }

}

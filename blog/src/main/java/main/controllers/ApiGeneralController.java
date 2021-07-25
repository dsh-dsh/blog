package main.controllers;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.servise.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGeneralController {

    @Autowired
    private InitResponse initResponse;

    @Autowired
    private SettingsService settingsService;

    @GetMapping("/api/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/api/settings")
    public SettingsResponse settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/api/tag")
    public ResponseEntity<String> tags() {
        return ResponseEntity.ok(
                "{\n" +
                "\"tags\":\n" +
                "[\n" +
                "{\"name\":\"Java\", \"weight\":1},\n" +
                "{\"name\":\"Spring\", \"weight\":0.56},\n" +
                "{\"name\":\"Hibernate\", \"weight\":0.22},\n" +
                "{\"name\":\"Hadoop\", \"weight\":0.17},\n" +
                "]\n" +
                "}");
    }

}

package main.controllers;

import main.api.responses.InitResponse;
import main.api.responses.SettingsResponse;
import main.api.responses.TagResponse;
import main.servises.SettingsService;
import main.servises.TagService;
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
    @Autowired
    private TagService tagService;

    @GetMapping("/api/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/api/settings")
    public SettingsResponse settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/api/tag")
    public ResponseEntity<TagResponse> tags() {

        TagResponse tagResponse = tagService.getTags();

        return ResponseEntity.ok(tagResponse);
    }

}

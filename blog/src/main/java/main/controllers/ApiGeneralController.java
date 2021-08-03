package main.controllers;

import main.api.responses.CalendarResponse;
import main.api.responses.InitResponse;
import main.api.responses.SettingsResponse;
import main.api.responses.TagResponse;
import main.dto.CalendarDTO;
import main.repositories.CalendarRepository;
import main.servises.CalendarService;
import main.servises.SettingsService;
import main.servises.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
public class ApiGeneralController {

    @Autowired
    private InitResponse initResponse;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CalendarService calendarService;


    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public SettingsResponse settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    public ResponseEntity<TagResponse> tags() {

        TagResponse tagResponse = tagService.getTags();
        return ResponseEntity.ok(tagResponse);

    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> calendar(@RequestParam int year) {

        CalendarResponse calendarResponse = calendarService.getCalendar();
        return ResponseEntity.ok(calendarResponse);

    }

}

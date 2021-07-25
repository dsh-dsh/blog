package main.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/auth")
public class ApiAuthController {

    @GetMapping("/check")
    public ResponseEntity<String> checkAuth() {

        return ResponseEntity.ok("{ \"result\": false }");

    }

}

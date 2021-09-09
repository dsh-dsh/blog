package main.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.io.IOException;
import java.net.URISyntaxException;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String index() throws IOException, URISyntaxException {
        return "index";
    }

    @GetMapping("/login/change-password/{hash}")
    public String changePassword() {
        return "index";
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{path:[^\\\\.]*}")
    public String redirectToIndex() {
        return "forward:/";
    }

}

package es.users.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    static final String REDIRECT = "redirect:/swagger-ui/index.html#";

    @GetMapping("/")
    public String root() {
        return REDIRECT;
    }


    @GetMapping("/swagger-ui")
    public String swaggerUi() {
        return REDIRECT;
    }


    @GetMapping("/swagger-ui/")
    public String swaggerUiSlash() {
        return REDIRECT;
    }


    @GetMapping("/integrartramitaapi")
    public String sialEntidades() {
        return REDIRECT;
    }
}

package mk.ukim.finki.ib.documentvalidator.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerController {
    @GetMapping("/hello")
    public String hello(){
        return "Successfully connected";
    }
}

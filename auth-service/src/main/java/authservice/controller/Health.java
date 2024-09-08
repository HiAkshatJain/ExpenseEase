package authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Health {
    @GetMapping("/api/v1/auth/health")
    public String init(){
        return "Auth Service is working";
    }
}

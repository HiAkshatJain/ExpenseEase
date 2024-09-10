package userservice.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Health {
    @GetMapping("/api/v1/user/health")
    public String init(){
        return "User Service is working";
    }
}

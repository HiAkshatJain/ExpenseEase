package expense.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Health {
    @GetMapping("/api/v1/expense/health")
    public String init(){
        return "Expense service is working";
    }
}

package xyz.evernet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.response.HealthCheckResponse;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public HealthCheckResponse healthCheck() {
        return HealthCheckResponse.ok();
    }
}

package org.evernet.vertex.controller;

import org.evernet.vertex.response.HealthCheckResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public HealthCheckResponse healthCheck() {
        return HealthCheckResponse.ok();
    }
}

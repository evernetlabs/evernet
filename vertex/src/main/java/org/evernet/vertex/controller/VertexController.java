package org.evernet.vertex.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.vertex.bean.Vertex;
import org.evernet.vertex.service.ConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VertexController {

    private final ConfigService configService;

    @GetMapping("/vertex")
    public Vertex get() {
        return configService.getVertex();
    }
}

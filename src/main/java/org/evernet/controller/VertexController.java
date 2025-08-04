package org.evernet.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.response.VertexInfoResponse;
import org.evernet.service.VertexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VertexController {

    private final VertexService vertexService;

    @GetMapping("/vertex/info")
    public VertexInfoResponse get() {
        return vertexService.getInfo();
    }
}

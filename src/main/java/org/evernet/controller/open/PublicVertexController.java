package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.Vertex;
import org.evernet.service.VertexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicVertexController {

    private final VertexService vertexService;

    @GetMapping("/vertex")
    public Vertex get() {
        return vertexService.get();
    }
}

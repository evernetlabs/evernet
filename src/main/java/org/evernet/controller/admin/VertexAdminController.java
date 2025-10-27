package org.evernet.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.bean.Vertex;
import org.evernet.service.VertexService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class VertexAdminController extends AuthenticatedAdminController {

    private final VertexService vertexService;

    @PutMapping("/vertex")
    public Vertex set(@Valid @RequestBody Vertex vertex) {
        return vertexService.set(vertex);
    }
}

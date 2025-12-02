package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Connection;
import org.evernet.service.ConnectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/nodes/{nodeIdentifier}/structure")
@RequiredArgsConstructor
public class PublicConnectionController {

    private final ConnectionService connectionService;


    @GetMapping("/connections")
    public List<Connection> list(@PathVariable String nodeIdentifier, @RequestParam String structureAddress) {
        return connectionService.list(nodeIdentifier, structureAddress);
    }
}

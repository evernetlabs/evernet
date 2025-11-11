package org.evernet.controller.open;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Node;
import org.evernet.service.NodeService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicNodeController {

    private final NodeService nodeService;

    @GetMapping("/nodes")
    public List<Node> listOpen(Pageable pageable) {
        return nodeService.listOpen(pageable);
    }

    @GetMapping("/nodes/{identifier}")
    public Node get(@PathVariable String identifier) {
        return nodeService.get(identifier);
    }
}

package org.evernet.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.model.MessageType;
import org.evernet.service.MessageTypeService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes/{nodeIdentifier}/messaging")
@RequiredArgsConstructor
public class PublicMessageTypeController {

    private final MessageTypeService messageTypeService;

    @GetMapping("/types")
    public List<MessageType> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return messageTypeService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/type")
    public MessageType get(@PathVariable String nodeIdentifier, @RequestParam String address) {
        return messageTypeService.get(nodeIdentifier, address);
    }
}

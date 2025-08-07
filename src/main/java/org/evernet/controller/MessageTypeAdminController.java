package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.MessageType;
import org.evernet.request.MessageTypeCloneRequest;
import org.evernet.request.MessageTypeCreationRequest;
import org.evernet.service.MessageTypeService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/messaging")
@RequiredArgsConstructor
public class MessageTypeAdminController extends AuthenticatedAdminController {

    private final MessageTypeService messageTypeService;

    @PostMapping("/types")
    public MessageType create(@PathVariable String nodeIdentifier, @Valid @RequestBody MessageTypeCreationRequest request) {
        return messageTypeService.create(
                nodeIdentifier,
                request,
                getAdminIdentifier()
        );
    }

    @GetMapping("/types")
    public List<MessageType> list(@PathVariable String nodeIdentifier, Pageable pageable) {
        return messageTypeService.list(nodeIdentifier, pageable);
    }

    @GetMapping("/type")
    public MessageType get(@PathVariable String nodeIdentifier, @RequestParam String address) {
        return messageTypeService.get(nodeIdentifier, address);
    }

    @DeleteMapping("/type")
    public MessageType delete(@PathVariable String nodeIdentifier, @RequestParam String address) {
        return messageTypeService.delete(nodeIdentifier, address);
    }

    @PostMapping("/types/clone")
    public MessageType copy(@PathVariable String nodeIdentifier, @Valid @RequestBody MessageTypeCloneRequest request) {
        return messageTypeService.copy(nodeIdentifier, request.getAddress(), getAdminIdentifier());
    }
}

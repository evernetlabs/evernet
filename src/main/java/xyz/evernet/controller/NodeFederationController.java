package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.auth.AuthenticatedVertexController;
import xyz.evernet.exception.ClientException;
import xyz.evernet.federation.event.NodeCreationEvent;
import xyz.evernet.request.NodeFederationRequest;
import xyz.evernet.service.NodeFederationService;
import xyz.evernet.util.Json;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeFederationController extends AuthenticatedVertexController {

    private final NodeFederationService nodeFederationService;

    @PostMapping("/nodes/federate")
    public void handle(@Valid @RequestBody String jsonBody) {
        NodeFederationRequest request = Json.decode(jsonBody, NodeFederationRequest.class);

        switch (request.getEventType()) {
            case NODE_CREATED -> {
                NodeCreationEvent event = Json.decode(Json.encode(request.getEvent()), NodeCreationEvent.class);
                nodeFederationService.receiveEvent(
                        event,
                        request.getTargetUserAddresses(),
                        getSourceVertexEndpoint(),
                        request.getRequesterAddress()
                );
            }

            case PROPERTY_SET, PROPERTY_UNSET, EVENT_RAISED, FUNCTION_CALLED, FUNCTION_EXECUTED -> throw new ClientException("Not implemented");

            default -> throw new ClientException("Unsupported event type %s".formatted(request.getEventType()));
        }
    }
}

package xyz.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.auth.AuthenticatedVertexController;
import xyz.evernet.exception.ClientException;
import xyz.evernet.federation.event.*;
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

            case NODE_DELETED -> {
                NodeDeletionEvent event = Json.decode(Json.encode(request.getEvent()), NodeDeletionEvent.class);
                nodeFederationService.receiveEvent(
                        event,
                        request.getTargetUserAddresses(),
                        getSourceVertexEndpoint(),
                        request.getRequesterAddress()
                );
            }

            case USER_ADDED -> {
                NodeUserAdditionEvent event = Json.decode(Json.encode(request.getEvent()), NodeUserAdditionEvent.class);
                nodeFederationService.receiveEvent(
                        event,
                        request.getTargetUserAddresses(),
                        getSourceVertexEndpoint(),
                        request.getRequesterAddress()
                );
            }

            case USER_DELETED -> {
                NodeUserDeletionEvent event = Json.decode(Json.encode(request.getEvent()), NodeUserDeletionEvent.class);
                nodeFederationService.receiveEvent(
                        event,
                        request.getTargetUserAddresses(),
                        getSourceVertexEndpoint(),
                        request.getRequesterAddress()
                );
            }

            case USER_UPDATED -> {
                NodeUserUpdateEvent event = Json.decode(Json.encode(request.getEvent()), NodeUserUpdateEvent.class);
                nodeFederationService.receiveEvent(
                        event,
                        request.getTargetUserAddresses(),
                        getSourceVertexEndpoint(),
                        request.getRequesterAddress()
                );
            }

            case PROPERTY_SET -> {
                NodePropertySetEvent event = Json.decode(Json.encode(request.getEvent()), NodePropertySetEvent.class);
                nodeFederationService.receiveEvent(
                        event,
                        request.getTargetUserAddresses(),
                        getSourceVertexEndpoint(),
                        request.getRequesterAddress()
                );
            }

            case PROPERTY_UNSET -> {
                NodePropertyUnsetEvent event = Json.decode(Json.encode(request.getEvent()), NodePropertyUnsetEvent.class);
                nodeFederationService.receiveEvent(
                        event,
                        request.getTargetUserAddresses(),
                        getSourceVertexEndpoint(),
                        request.getRequesterAddress()
                );
            }

            case EVENT_RAISED -> {
                NodeEventRaisedEvent event = Json.decode(Json.encode(request.getEvent()), NodeEventRaisedEvent.class);
                nodeFederationService.receiveEvent(
                        event,
                        request.getTargetUserAddresses(),
                        getSourceVertexEndpoint(),
                        request.getRequesterAddress()
                );
            }

            case RELATIONSHIP_SET, RELATIONSHIP_UNSET, FUNCTION_CALLED, FUNCTION_EXECUTED -> throw new ClientException("Not implemented");

            default -> throw new ClientException("Unsupported event type %s".formatted(request.getEventType()));
        }
    }
}

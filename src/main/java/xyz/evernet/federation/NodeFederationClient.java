package xyz.evernet.federation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.evernet.auth.Jwt;
import xyz.evernet.exception.ServerException;
import xyz.evernet.federation.event.FederationEventEnvelope;
import xyz.evernet.service.ConfigReaderService;

@Component
@RequiredArgsConstructor
public class NodeFederationClient {

    private final RestTemplate restTemplate;

    private final ConfigReaderService  configReaderService;

    private final Jwt jwt;

    public void send(FederationEventEnvelope<?> envelope, String targetVertexEndpoint) throws Exception {
        String federationToken = jwt.getVertexToken(targetVertexEndpoint);

        String url = "%s://%s/api/v1/nodes/federate".formatted(configReaderService.getFederationProtocol(), targetVertexEndpoint);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(federationToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<FederationEventEnvelope<?>>(envelope, headers),
                Void.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ServerException("Error while sending node federation message to %s".formatted(targetVertexEndpoint));
        }
    }
}

package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.evernet.embedded.Property;
import xyz.evernet.exception.ClientException;
import xyz.evernet.exception.NotAllowedException;
import xyz.evernet.exception.NotFoundException;
import xyz.evernet.model.Node;
import xyz.evernet.model.Structure;
import xyz.evernet.repository.NodeRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NodeHelperService {

    private final NodeRepository nodeRepository;

    private final StructureRouterService structureRouterService;

    private final MongoTemplate mongoTemplate;

    public Node save(Node node) {
        return nodeRepository.save(node);
    }

    public Node findByAddress(String address) {
        return nodeRepository.findByAddress(address);
    }

    public void delete(String address) {
        nodeRepository.deleteByAddress(address);
    }

    public Node setProperty(String address, String propertyIdentifier, Object value, String requesterAddress) {
        Node node = findByAddress(address);

        Structure structure = structureRouterService.get(node.getStructureAddress());

        if (!hasPropertyRole(node, structure, requesterAddress, propertyIdentifier)) {
            throw new NotAllowedException();
        }

        Object validatedValue = structure.validateProperty(propertyIdentifier, value);

        node = mongoTemplate.findAndModify(
                Query.query(
                        Criteria.where("address").is(address)
                ),
                new Update()
                        .set("properties.%s".formatted(propertyIdentifier), validatedValue)
                        .set("updatedAt", Instant.now()),
                FindAndModifyOptions.options().returnNew(true),
                Node.class
        );

        if (node == null) {
            throw new NotFoundException("Node %s not found".formatted(address));
        }

        return node;
    }

    public Node unsetProperty(String address, String propertyIdentifier, String requesterAddress) {

        Node node = findByAddress(address);

        Structure structure = structureRouterService.get(node.getStructureAddress());

        if (!hasPropertyRole(node, structure, requesterAddress, propertyIdentifier)) {
            throw new NotAllowedException();
        }

        if (!structure.getProperties().containsKey(propertyIdentifier)) {
            throw new ClientException("Property %s not found in structure %s".formatted(propertyIdentifier, structure.getIdentifier()));
        }

        node = mongoTemplate.findAndModify(
                Query.query(
                        Criteria.where("address").is(address)
                ),
                new Update()
                        .unset("properties.%s".formatted(propertyIdentifier))
                        .set("updatedAt", Instant.now()),
                FindAndModifyOptions.options().returnNew(true),
                Node.class
        );

        if (node == null) {
            throw new NotFoundException("Node %s not found".formatted(address));
        }

        return node;
    }

    public Boolean hasManagementRole(String nodeAddress, String userAddress) {
        Node node = nodeRepository.findByAddress(nodeAddress);

        if (node == null) {
            return false;
        }

        return hasManagementRole(node, userAddress);
    }

    public Boolean hasManagementRole(Node node, String userAddress) {
        return hasManagementRole(node, structureRouterService.get(node.getStructureAddress()), userAddress);
    }

    public Boolean hasManagementRole(Node node, Structure structure, String userAddress) {

        Map<String, Set<String>> users = node.getUsers();

        if (users == null) {
            return false;
        }

        Set<String> userRoles = users.get(userAddress);
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }

        Set<String> structureManagementRoles = structure.getManagementRoles();

        if (structureManagementRoles == null || structureManagementRoles.isEmpty()) {
            return false;
        }

        return !Collections.disjoint(structureManagementRoles, userRoles);
    }

    public Boolean hasPropertyRole(Node node, Structure structure, String userAddress, String propertyIdentifier) {
        Map<String, Set<String>> users = node.getUsers();

        if (users == null) {
            return false;
        }

        if (!users.containsKey(userAddress)) {
            return false;
        }

        Property propertySchema = structure.getProperties().get(propertyIdentifier);

        if (propertySchema == null) {
            return false;
        }

        if (CollectionUtils.isEmpty(propertySchema.getAllowedRoles())) {
            return true;
        }

        Set<String> propertyRoles = propertySchema.getAllowedRoles();

        Set<String> userRoles = users.get(userAddress);

        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }

        return !Collections.disjoint(propertyRoles, userRoles);
    }
}
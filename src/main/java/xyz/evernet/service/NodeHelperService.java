package xyz.evernet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.evernet.model.Node;
import xyz.evernet.model.Structure;
import xyz.evernet.repository.NodeRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NodeHelperService {

    private final NodeRepository nodeRepository;

    private final StructureRouterService structureRouterService;

    public Node save(Node node) {
        return nodeRepository.save(node);
    }

    public Node findByAddress(String address) {
        return nodeRepository.findByAddress(address);
    }

    public void delete(String address) {
        nodeRepository.deleteByAddress(address);
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
}